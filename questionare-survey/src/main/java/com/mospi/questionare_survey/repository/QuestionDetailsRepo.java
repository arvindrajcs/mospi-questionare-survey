package com.mospi.questionare_survey.repository;

import com.mospi.questionare_survey.entity.QuestionDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionDetailsRepo extends JpaRepository<QuestionDetails, Long> {

    // Kisi specific block ke saare questions display order ke hisaab se fetch karne ke liye
    List<QuestionDetails> findByBlock_BlockIdOrderByDisplayOrderAsc(Long blockId);

    // Saare active questions fetch karne ke liye
    List<QuestionDetails> findByIsActiveTrueOrderByDisplayOrderAsc();
}