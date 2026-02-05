package com.finrating.service;

import com.finrating.dto.DashboardSummary;
import com.finrating.dto.FilterRequest;
import com.finrating.entity.Dataset;
import com.finrating.entity.FinancialRecord;
import com.finrating.entity.User;
import com.finrating.repository.DatasetRepository;
import com.finrating.repository.FinancialRecordRepository;
import com.finrating.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DashboardService {

    private final DatasetRepository datasetRepository;
    private final FinancialRecordRepository financialRecordRepository;
    private final UserRepository userRepository;

    /* ======================================================
       PUBLIC API (EMAIL-BASED — REQUIRED FOR JWT)
       ====================================================== */

    public DashboardSummary getDashboardSummaryByEmail(String email) {
        User user = getUserByEmail(email);
        return buildDashboardSummary(user);
    }

    public Page<FinancialRecord> getRecordsByEmail(String email, int page, int size) {
        User user = getUserByEmail(email);
        return getRecordsForUser(user, page, size);
    }

    public Page<FinancialRecord> filterRecordsByEmail(FilterRequest filterRequest, String email) {
        User user = getUserByEmail(email);
        return filterRecordsForUser(filterRequest, user);
    }

    /* ======================================================
       CORE LOGIC (UNCHANGED, SAFE)
       ====================================================== */

    private DashboardSummary buildDashboardSummary(User user) {
        log.info("Getting dashboard summary for user: {}", user.getEmail());

        List<Dataset> userDatasets = getUserDatasets(user);
        Long totalRecords = financialRecordRepository.countByDatasetIn(userDatasets);

        Map<String, Long> ratingDistribution = getRatingDistribution(userDatasets);

        return DashboardSummary.builder()
                .totalRecords(totalRecords)
                .datasetCount((long) userDatasets.size())
                .ratingDistribution(ratingDistribution)
                .build();
    }

    private Page<FinancialRecord> getRecordsForUser(User user, int page, int size) {
        log.info("Getting records for user: {}", user.getEmail());

        List<Dataset> userDatasets = getUserDatasets(user);
        if (userDatasets.isEmpty()) {
            return Page.empty();
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "calculatedAt")
        );

        return financialRecordRepository.findByDatasetIn(userDatasets, pageable);
    }

    private Page<FinancialRecord> filterRecordsForUser(FilterRequest filterRequest, User user) {
        List<Dataset> userDatasets = getUserDatasets(user);
        if (userDatasets.isEmpty()) {
            return Page.empty();
        }

        Specification<FinancialRecord> spec =
                buildFilterSpecification(filterRequest, userDatasets);

        Sort sort = buildSort(filterRequest);
        Pageable pageable = PageRequest.of(
                filterRequest.getPage(),
                filterRequest.getSize(),
                sort
        );

        return financialRecordRepository.findAll(spec, pageable);
    }

    /* ======================================================
       HELPERS
       ====================================================== */

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }

    private List<Dataset> getUserDatasets(User user) {
        return datasetRepository.findByUserOrderByUploadedAtDesc(user);
    }

    private Map<String, Long> getRatingDistribution(List<Dataset> userDatasets) {
        if (userDatasets.isEmpty()) return Map.of();

        return financialRecordRepository.countByRating(userDatasets)
                .stream()
                .collect(Collectors.toMap(
                        m -> m.get("rating").toString(),
                        m -> ((Number) m.get("count")).longValue()
                ));
    }

    private Specification<FinancialRecord> buildFilterSpecification(
            FilterRequest filterRequest,
            List<Dataset> userDatasets) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(root.get("dataset").in(userDatasets));

            if (filterRequest.getSearchKeyword() != null && !filterRequest.getSearchKeyword().isBlank()) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("issuerName")),
                                "%" + filterRequest.getSearchKeyword().toLowerCase() + "%"
                        )
                );
            }

            if (filterRequest.getIndustries() != null && !filterRequest.getIndustries().isEmpty()) {
                predicates.add(root.get("industry").in(filterRequest.getIndustries()));
            }

            if (filterRequest.getCountries() != null && !filterRequest.getCountries().isEmpty()) {
                predicates.add(root.get("country").in(filterRequest.getCountries()));
            }

            if (filterRequest.getRatings() != null && !filterRequest.getRatings().isEmpty()) {
                predicates.add(root.get("rating").in(filterRequest.getRatings()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Sort buildSort(FilterRequest filterRequest) {
        Sort.Direction direction =
                "DESC".equalsIgnoreCase(filterRequest.getSortDirection())
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        return Sort.by(direction, filterRequest.getSortBy());
    }
}
