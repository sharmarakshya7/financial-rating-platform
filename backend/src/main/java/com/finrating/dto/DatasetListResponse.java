package com.finrating.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DatasetListResponse {
    private Long id;
    private String name;
    private Integer recordCount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime uploadedAt;
}
