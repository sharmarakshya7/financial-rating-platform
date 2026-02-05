package com.finrating.dto;

import lombok.Data;
import java.util.List;

@Data
public class FilterRequest {
    private int page = 0;
    private int size = 20;

    private List<String> industries;
    private List<String> countries;
    private List<String> ratings;

    private String searchKeyword;

    private String sortBy = "calculatedAt";
    private String sortDirection = "DESC";
}
