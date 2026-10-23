package com.mospi.questionare_survey.repository;

import com.mospi.questionare_survey.entity.BlockDetails;
import com.mospi.questionare_survey.entity.ScheduleDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlockDetailsRepo extends JpaRepository<BlockDetails, Long> {
    Page<BlockDetails> findByCreatedBy(Long userId, Pageable pageable);
    Page<BlockDetails> findBySurvey_SurveyId(Long surveyId, Pageable pageable);
    List<BlockDetails> findAllBySurvey_SurveyId(Long surveyId);


    boolean existsByCreatedByAndStatus_StatusIdIn(Long userId, List<Long> longs);
}