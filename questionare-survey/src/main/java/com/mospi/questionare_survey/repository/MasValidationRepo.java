package com.mospi.questionare_survey.repository;

import com.mospi.questionare_survey.entity.MasComparison;
import com.mospi.questionare_survey.entity.MasValidation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MasValidationRepo extends JpaRepository<MasValidation, Long> {
    Optional<MasValidation> findByDescription(String description);
    Page<MasComparison> findByCreatedBy(Long userId, Pageable pageable);

}
