package com.mospi.questionare_survey.repository;

import com.mospi.questionare_survey.entity.SurveyDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SurveyDetailsRepo extends JpaRepository<SurveyDetails, Long> {

    Page<SurveyDetails> findByCreatedBy(Long userId, Pageable pageable);

    boolean existsByCreatedByAndStatus_StatusIdIn(Long createdBy, List<Long> statusIds);

    Page<SurveyDetails> findByStatus_StatusIdAndCreatedBy(Long statusId, Long createdBy, Pageable pageable);

}