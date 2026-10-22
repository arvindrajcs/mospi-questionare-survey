package com.mospi.questionare_survey.repository;

import com.mospi.questionare_survey.entity.QuestionDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionDetailsRepo extends JpaRepository<QuestionDetails, Long> {

    Page<QuestionDetails> findByCreatedBy(Long userId, Pageable pageable);
}