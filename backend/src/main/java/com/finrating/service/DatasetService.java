package com.finrating.service;

import com.finrating.dto.DatasetUploadResponse;
import com.finrating.entity.Dataset;
import com.finrating.entity.User;
import com.finrating.kafka.KafkaProducerService;
import com.finrating.repository.DatasetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DatasetService {

    private final DatasetRepository datasetRepository;
    private final KafkaProducerService kafkaProducer;

    @Value("${app.upload.dir:uploads}")
    private String uploadDirectory;

    @Value("${app.upload.max-file-size:52428800}")
    private long maxFileSize;

    private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList("csv", "xlsx", "xls");
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "text/csv",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    );

    public DatasetUploadResponse uploadDataset(MultipartFile file, User user) throws IOException {
        log.info("Starting dataset upload for user: {}", user.getEmail());

        validateFile(file);

        Dataset dataset = createDatasetRecord(file, user);
        dataset = datasetRepository.save(dataset);

        try {
            String filePath = saveFileToDisk(file, dataset.getId());
            dataset.setFilePath(filePath);
            dataset = datasetRepository.save(dataset);

            sendToKafkaProcessing(dataset.getId());

            return buildSuccessResponse(dataset);

        } catch (Exception e) {
            log.error("Error processing dataset upload", e);
            handleUploadFailure(dataset);
            throw new RuntimeException("Failed to process dataset: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public List<Dataset> getUserDatasets(User user) {
        return datasetRepository.findByUserOrderByUploadedAtDesc(user);
    }

    @Transactional(readOnly = true)
    public Dataset getDatasetById(Long id, User user) {
        Dataset dataset = datasetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dataset not found with ID: " + id));
        validateUserOwnership(dataset, user);
        return dataset;
    }

    public void deleteDataset(Long id, User user) {
        Dataset dataset = datasetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dataset not found with ID: " + id));

        validateUserOwnership(dataset, user);
        deletePhysicalFile(dataset.getFilePath());
        datasetRepository.delete(dataset);
    }

    public void updateDatasetStatus(Long datasetId, Dataset.ProcessingStatus status, Integer recordCount) {
        Dataset dataset = datasetRepository.findById(datasetId)
                .orElseThrow(() -> new RuntimeException("Dataset not found with ID: " + datasetId));

        dataset.setStatus(status);

        if (recordCount != null) {
            dataset.setRecordCount(recordCount);
        }

        if (status == Dataset.ProcessingStatus.COMPLETED) {
            dataset.setProcessedAt(LocalDateTime.now());
        }

        datasetRepository.save(dataset);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("File cannot be empty");
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty())
            throw new IllegalArgumentException("Invalid filename");

        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException(
                    String.format("File size exceeds maximum allowed size of %d MB", maxFileSize / (1024 * 1024))
            );
        }

        String fileExtension = getFileExtension(originalFilename);
        if (!ALLOWED_FILE_TYPES.contains(fileExtension.toLowerCase())) {
            throw new IllegalArgumentException("Unsupported file type: " + fileExtension);
        }

        String contentType = file.getContentType();
        if (contentType != null && !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            log.warn("Unexpected content type: {}", contentType);
        }
    }

    private Dataset createDatasetRecord(MultipartFile file, User user) {
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);

        return Dataset.builder()
                .name(originalFilename)
                .fileName(originalFilename)
                .fileType(fileExtension.toLowerCase())
                .fileSize(file.getSize())
                .user(user)
                .status(Dataset.ProcessingStatus.PENDING)
                .recordCount(0)
                .build();
    }

    private String saveFileToDisk(MultipartFile file, Long datasetId) throws IOException {
        Path uploadDir = Paths.get(uploadDirectory);
        if (!Files.exists(uploadDir)) Files.createDirectories(uploadDir);

        String uniqueFilename = String.format("%d_%s_%s",
                datasetId,
                UUID.randomUUID().toString().substring(0, 8),
                file.getOriginalFilename()
        );

        Path filePath = uploadDir.resolve(uniqueFilename);
        Files.write(filePath, file.getBytes());
        return filePath.toString();
    }

    private void sendToKafkaProcessing(Long datasetId) {
        kafkaProducer.sendDatasetProcessingMessage(datasetId);
    }

    private DatasetUploadResponse buildSuccessResponse(Dataset dataset) {
        return DatasetUploadResponse.builder()
                .id(dataset.getId()) // <-- FIXED
                .name(dataset.getName())
                .status(dataset.getStatus().name())
                .message("Dataset uploaded successfully. Processing started.")
                .build();
    }

    private void handleUploadFailure(Dataset dataset) {
        try {
            dataset.setStatus(Dataset.ProcessingStatus.FAILED);
            datasetRepository.save(dataset);
        } catch (Exception ex) {
            log.error("Failed to update dataset status after upload failure", ex);
        }
    }

    private void validateUserOwnership(Dataset dataset, User user) {
        if (!dataset.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized: You do not have access to this dataset");
        }
    }

    private void deletePhysicalFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) return;
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) Files.delete(path);
        } catch (IOException e) {
            log.error("Failed to delete file: {}", filePath, e);
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new IllegalArgumentException("Invalid filename: no extension found");
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}
