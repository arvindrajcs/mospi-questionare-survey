package com.mospi.questionare_survey.repository;

import com.mospi.questionare_survey.entity.TemplateDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface TemplateDetailsRepo extends JpaRepository<TemplateDetails, Long> {
    Optional<TemplateDetails> findByTemplateId(Long templateId);
    Page<TemplateDetails> findByCreatedBy(Long userId, Pageable pageable);
}
