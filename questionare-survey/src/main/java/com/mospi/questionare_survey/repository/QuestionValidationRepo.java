package com.mospi.questionare_survey.repository;

import com.mospi.questionare_survey.entity.QuestionValidation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionValidationRepo extends JpaRepository<QuestionValidation, Long> {

}
