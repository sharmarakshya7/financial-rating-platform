package com.finrating.dto;

import lombok.*;

@Data
@Builder
public class DatasetUploadResponse {
    private Long id;
    private String name;
    private String status;
    private String message;
}
