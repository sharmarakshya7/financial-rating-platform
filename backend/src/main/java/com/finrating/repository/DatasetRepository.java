package com.finrating.repository;

import com.finrating.entity.Dataset;
import com.finrating.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DatasetRepository extends JpaRepository<Dataset, Long> {
    List<Dataset> findByUserOrderByUploadedAtDesc(User user);
    Long countByUser(User user);
}
