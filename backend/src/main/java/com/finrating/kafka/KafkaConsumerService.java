package com.finrating.kafka;

import com.finrating.entity.Dataset;
import com.finrating.entity.FinancialRecord;
import com.finrating.repository.DatasetRepository;
import com.finrating.repository.FinancialRecordRepository;
import com.finrating.service.RatingService;
import com.opencsv.CSVReader;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private static final int BATCH_SIZE = 500;

    private final DatasetRepository datasetRepository;
    private final FinancialRecordRepository financialRecordRepository;
    private final RatingService ratingService;

    @KafkaListener(topics = "dataset-processing", groupId = "financial-rating-group")
    @Transactional
    public void processDataset(String message) {

        Long datasetId = Long.valueOf(message);

        System.out.println("=== KAFKA START ===");
        System.out.println("Dataset ID: " + datasetId);

        Dataset dataset = datasetRepository.findById(datasetId)
                .orElseThrow(() -> new RuntimeException("Dataset not found: " + datasetId));

        System.out.println("File path: " + dataset.getFilePath());

        try {
            /* ===================== MARK PROCESSING ===================== */
            dataset.setStatus(Dataset.ProcessingStatus.PROCESSING);
            datasetRepository.save(dataset);

            File file = new File(dataset.getFilePath());
            if (!file.exists()) {
                throw new RuntimeException("File not found at path: " + dataset.getFilePath());
            }

            int recordCount;
            String fileType = dataset.getFileType().toLowerCase();

            if (fileType.equals("csv")) {
                recordCount = processCSV(file, dataset);
            } else if (fileType.equals("xlsx") || fileType.equals("xls")) {
                recordCount = processExcel(file, dataset);
            } else {
                throw new RuntimeException("Unsupported file type: " + fileType);
            }

            /* ===================== MARK COMPLETED ===================== */
            dataset.setStatus(Dataset.ProcessingStatus.COMPLETED);
            dataset.setProcessedAt(LocalDateTime.now());
            dataset.setRecordCount(recordCount);
            datasetRepository.save(dataset);

            System.out.println("=== PROCESSING COMPLETED ===");
            System.out.println("Total records: " + recordCount);

        } catch (Exception e) {
            dataset.setStatus(Dataset.ProcessingStatus.FAILED);
            datasetRepository.save(dataset);
            e.printStackTrace();
            throw new RuntimeException("Dataset processing failed", e);
        }
    }

    /* ===================== CSV PROCESSING (BATCHED) ===================== */

    private int processCSV(File file, Dataset dataset) throws Exception {

        List<FinancialRecord> batch = new ArrayList<>();
        int count = 0;

        try (CSVReader reader = new CSVReader(new FileReader(file))) {

            reader.readNext(); // skip header
            String[] line;

            while ((line = reader.readNext()) != null) {

                FinancialRecord record = parseRecordFromArray(line, dataset);
                ratingService.calculateRating(record);
                record.setCalculatedAt(LocalDateTime.now());

                batch.add(record);
                count++;

                if (batch.size() == BATCH_SIZE) {
                    financialRecordRepository.saveAll(batch);
                    batch.clear();
                    System.out.println("Saved " + count + " records");
                }
            }
        }

        if (!batch.isEmpty()) {
            financialRecordRepository.saveAll(batch);
            System.out.println("Saved final batch. Total: " + count);
        }

        return count;
    }

    /* ===================== EXCEL PROCESSING (BATCHED) ===================== */

    private int processExcel(File file, Dataset dataset) throws Exception {

        List<FinancialRecord> batch = new ArrayList<>();
        int count = 0;

        try (Workbook workbook = new XSSFWorkbook(new FileInputStream(file))) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);
                if (row == null) continue;

                FinancialRecord record = parseRecordFromRow(row, dataset);
                ratingService.calculateRating(record);
                record.setCalculatedAt(LocalDateTime.now());

                batch.add(record);
                count++;

                if (batch.size() == BATCH_SIZE) {
                    financialRecordRepository.saveAll(batch);
                    batch.clear();
                    System.out.println("Saved " + count + " records");
                }
            }
        }

        if (!batch.isEmpty()) {
            financialRecordRepository.saveAll(batch);
            System.out.println("Saved final batch. Total: " + count);
        }

        return count;
    }

    /* ===================== PARSERS ===================== */

    private FinancialRecord parseRecordFromArray(String[] data, Dataset dataset) {
        return FinancialRecord.builder()
                .dataset(dataset)
                .issuerName(getString(data, 0))
                .industry(getString(data, 1))
                .country(getString(data, 2))
                .revenue(getDecimal(data, 3))
                .ebitda(getDecimal(data, 4))
                .totalDebt(getDecimal(data, 5))
                .interestExpense(getDecimal(data, 6))
                .currentAssets(getDecimal(data, 7))
                .currentLiabilities(getDecimal(data, 8))
                .build();
    }

    private FinancialRecord parseRecordFromRow(Row row, Dataset dataset) {
        return FinancialRecord.builder()
                .dataset(dataset)
                .issuerName(getCellString(row.getCell(0)))
                .industry(getCellString(row.getCell(1)))
                .country(getCellString(row.getCell(2)))
                .revenue(getCellDecimal(row.getCell(3)))
                .ebitda(getCellDecimal(row.getCell(4)))
                .totalDebt(getCellDecimal(row.getCell(5)))
                .interestExpense(getCellDecimal(row.getCell(6)))
                .currentAssets(getCellDecimal(row.getCell(7)))
                .currentLiabilities(getCellDecimal(row.getCell(8)))
                .build();
    }

    /* ===================== HELPERS ===================== */

    private String getString(String[] arr, int index) {
        if (arr.length <= index) return null;
        String value = arr[index];
        return value == null || value.trim().isEmpty() ? null : value;
    }

    private BigDecimal getDecimal(String[] arr, int index) {
        if (arr.length <= index) return null;
        String value = arr[index];
        if (value == null || value.trim().isEmpty()) return null;
        return new BigDecimal(value);
    }

    private String getCellString(Cell cell) {
        if (cell == null) return null;
        return cell.getCellType() == CellType.STRING
                ? cell.getStringCellValue()
                : String.valueOf(cell.getNumericCellValue());
    }

    private BigDecimal getCellDecimal(Cell cell) {
        if (cell == null || cell.getCellType() != CellType.NUMERIC) return null;
        return BigDecimal.valueOf(cell.getNumericCellValue());
    }
}
