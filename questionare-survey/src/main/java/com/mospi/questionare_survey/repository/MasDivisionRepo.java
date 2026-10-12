package com.mospi.questionare_survey.repository;

import com.mospi.questionare_survey.entity.MasDivision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MasDivisionRepo extends JpaRepository<MasDivision, Long> {

    // Description se division find karne ke liye
    Optional<MasDivision> findByDescription(String description);

    // Saare active divisions fetch karne ke liye
    List<MasDivision> findByIsActiveTrue();

    // Check karne ke liye ki description already exist karta hai ya nahi
    boolean existsByDescription(String description);
}