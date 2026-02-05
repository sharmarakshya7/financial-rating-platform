package com.finrating.dto;

import lombok.*;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardSummary {
    private Long totalRecords;
    private Map<String, Long> ratingDistribution;
    private Map<String, Long> categoryDistribution;
    private Long datasetCount;
}
