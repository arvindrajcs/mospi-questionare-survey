package com.mospi.questionare_survey.repository;

import com.mospi.questionare_survey.entity.MasScheduleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MasScheduleTypeRepo extends JpaRepository<MasScheduleType, Long> {

    // Description se schedule type find karne ke liye
    Optional<MasScheduleType> findByDescription(String description);

    // Saare active schedule types fetch karne ke liye
    List<MasScheduleType> findByIsActiveTrue();

    // Check karne ke liye ki description already exist karta hai ya nahi
    boolean existsByDescription(String description);
}