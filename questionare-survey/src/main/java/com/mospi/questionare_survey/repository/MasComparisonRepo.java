package com.mospi.questionare_survey.repository;

import com.mospi.questionare_survey.entity.MasComparison;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MasComparisonRepo extends JpaRepository<MasComparison, Long> {
    Optional<MasComparison> findByDescription(String description);
    Page<MasComparison> findByCreatedBy(Long userId, Pageable pageable);
}
