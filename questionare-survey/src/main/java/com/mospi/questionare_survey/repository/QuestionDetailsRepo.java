package com.mospi.questionare_survey.repository;

import com.mospi.questionare_survey.entity.QuestionDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionDetailsRepo extends JpaRepository<QuestionDetails, Long> {

    Page<QuestionDetails> findByCreatedBy(Long userId, Pageable pageable);
    Page<QuestionDetails> findBySurvey_SurveyId(Long surveyId, Pageable pageable);
    Page<QuestionDetails> findByBlock_BlockId(Long blockId, Pageable pageable);
    List<QuestionDetails> findAllBySurvey_SurveyId(Long surveyId);


    boolean existsByCreatedByAndStatus_StatusIdIn(Long userId, List<Long> longs);
}