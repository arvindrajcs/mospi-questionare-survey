package com.mospi.questionare_survey.repository;

import com.mospi.questionare_survey.entity.AssignDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignDetailsRepo extends JpaRepository<AssignDetails, Long> {
    List<AssignDetails> findByAssignToAndIsActive(Long assignTo, boolean isActive);
}
