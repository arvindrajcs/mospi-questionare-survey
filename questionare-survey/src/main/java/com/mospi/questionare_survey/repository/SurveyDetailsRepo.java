package com.mospi.questionare_survey.repository;

import com.mospi.questionare_survey.entity.SurveyDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SurveyDetailsRepo extends JpaRepository<SurveyDetails, Long> {

    // Survey code se survey find karne ke liye
    Optional<SurveyDetails> findBySurveyCode(String surveyCode);

    // Check karne ke liye ki survey code already exist karta hai ya nahi
    boolean existsBySurveyCode(String surveyCode);

    // Kisi specific division ke saare surveys fetch karne ke liye
    List<SurveyDetails> findByDivision_DivisionId(Long divisionId);

    // Kisi specific survey type ke saare surveys fetch karne ke liye
    List<SurveyDetails> findBySurveyType_SurveyTypeId(Long surveyTypeId);

    // Saare active surveys fetch karne ke liye
    List<SurveyDetails> findByIsActiveTrue();
}