package com.mospi.questionare_survey.repository;

import com.mospi.questionare_survey.entity.MasStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MasStatusRepo extends JpaRepository<MasStatus, Long> {
}
