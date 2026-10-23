package com.mospi.questionare_survey.repository;

import com.mospi.questionare_survey.entity.ScheduleDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleDetailsRepo extends JpaRepository<ScheduleDetails, Long> {

    Page<ScheduleDetails> findByCreatedBy(Long userId, Pageable pageable);
    Page<ScheduleDetails> findBySurvey_SurveyId(Long surveyId, Pageable pageable);
}