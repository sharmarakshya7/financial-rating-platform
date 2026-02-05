package com.finrating.repository;

import com.finrating.entity.FinancialRecord;
import com.finrating.entity.Dataset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Map;

@Repository
public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long>,
        JpaSpecificationExecutor<FinancialRecord> {
    
    Page<FinancialRecord> findByDatasetIn(java.util.List<Dataset> datasets, Pageable pageable);
    
    Long countByDatasetIn(java.util.List<Dataset> datasets);
    
    @Query("SELECT f.rating as rating, COUNT(f) as count FROM FinancialRecord f " +
           "WHERE f.dataset IN :datasets GROUP BY f.rating")
    java.util.List<Map<String, Object>> countByRating(java.util.List<Dataset> datasets);
}
