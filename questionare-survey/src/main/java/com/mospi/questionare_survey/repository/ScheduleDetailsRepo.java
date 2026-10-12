package com.mospi.questionare_survey.repository;

import com.mospi.questionare_survey.entity.ScheduleDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleDetailsRepo extends JpaRepository<ScheduleDetails, Long> {

    // Kisi specific survey ke saare schedules fetch karne ke liye
    List<ScheduleDetails> findBySurvey_SurveyId(Long surveyId);

    // Kisi specific schedule type ke saare schedules fetch karne ke liye
    List<ScheduleDetails> findByScheduleType_ScheduleTypeId(Long scheduleTypeId);

    // Saare active schedules fetch karne ke liye
    List<ScheduleDetails> findByIsActiveTrue();
}