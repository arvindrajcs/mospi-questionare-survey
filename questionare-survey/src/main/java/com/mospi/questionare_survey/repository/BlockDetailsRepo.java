package com.mospi.questionare_survey.repository;

import com.mospi.questionare_survey.entity.BlockDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlockDetailsRepo extends JpaRepository<BlockDetails, Long> {
    Page<BlockDetails> findByCreatedBy(Long userId, Pageable pageable);

}