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

    // Fetch surveys created by a specific user with pagination
    Page<SurveyDetails> findByCreatedBy(Long userId, Pageable pageable);

    // Check if user has any active surveys in specific statuses (e.g., Draft or Pending)
    boolean existsByCreatedByAndStatus_StatusIdIn(Long createdBy, List<Long> statusIds);

    // Fetch surveys filtered by status ID and created by user with pagination
    Page<SurveyDetails> findByStatus_StatusIdAndCreatedBy(Long statusId, Long createdBy, Pageable pageable);

    // Corrected method for fetching surveys filtered strictly by status ID with pagination
    Page<SurveyDetails> findByStatus_StatusId(Long statusId, Pageable pageable);

    Page<SurveyDetails> findBySurveyIdInAndStatus_StatusId(List<Long> surveyIds, Long statusId, Pageable pageable);

    Optional<SurveyDetails> findBySurveyId(Long surveyId);

}