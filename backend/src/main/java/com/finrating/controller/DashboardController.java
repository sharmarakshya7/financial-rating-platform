package com.finrating.controller;

import com.finrating.dto.DashboardSummary;
import com.finrating.dto.FilterRequest;
import com.finrating.entity.FinancialRecord;
import com.finrating.entity.User;
import com.finrating.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummary> getDashboardSummary(
            @AuthenticationPrincipal(expression = "username") String email) {

        return ResponseEntity.ok(dashboardService.getDashboardSummaryByEmail(email));
    }


    @GetMapping("/records")
    public ResponseEntity<Page<FinancialRecord>> getRecords(
            @AuthenticationPrincipal(expression = "username") String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<FinancialRecord> records =
                dashboardService.getRecordsByEmail(email, page, size);

        return ResponseEntity.ok(records);
    }


    @PostMapping("/filter")
    public ResponseEntity<Page<FinancialRecord>> filterRecords(
            @RequestBody FilterRequest filterRequest,
            @AuthenticationPrincipal(expression = "username") String email) {

        return ResponseEntity.ok(
                dashboardService.filterRecordsByEmail(filterRequest, email)
        );
    }
}

