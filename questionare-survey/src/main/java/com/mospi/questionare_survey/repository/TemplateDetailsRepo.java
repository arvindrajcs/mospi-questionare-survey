package com.mospi.questionare_survey.repository;

import com.mospi.questionare_survey.entity.TemplateDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateDetailsRepo extends JpaRepository<TemplateDetails, Long> {
}
