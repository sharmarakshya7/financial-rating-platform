package com.finrating.controller;

import com.finrating.dto.DatasetUploadResponse;
import com.finrating.entity.Dataset;
import com.finrating.entity.User;
import com.finrating.service.DatasetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/datasets")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class DatasetController {

    private final DatasetService datasetService;

    @PostMapping("/upload")
    public ResponseEntity<DatasetUploadResponse> uploadDataset(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user) throws IOException {

        DatasetUploadResponse response = datasetService.uploadDataset(file, user);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<Dataset>> getUserDatasets(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(datasetService.getUserDatasets(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dataset> getDatasetById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(datasetService.getDatasetById(id, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDataset(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        datasetService.deleteDataset(id, user);
        return ResponseEntity.noContent().build();
    }
}
