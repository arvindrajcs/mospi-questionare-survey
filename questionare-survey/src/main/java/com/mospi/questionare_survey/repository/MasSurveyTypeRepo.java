package com.mospi.questionare_survey.repository;

import com.mospi.questionare_survey.entity.MasSurveyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MasSurveyTypeRepo extends JpaRepository<MasSurveyType, Long> {

    // Description se survey type find karne ke liye
    Optional<MasSurveyType> findByDescription(String description);

    // Saare active survey types fetch karne ke liye
    List<MasSurveyType> findByIsActiveTrue();

    // Check karne ke liye ki description already exist karta hai ya nahi
    boolean existsByDescription(String description);
}