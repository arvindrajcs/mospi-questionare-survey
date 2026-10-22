package com.mospi.questionare_survey.repository;

import com.mospi.questionare_survey.entity.SkipCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkipConditionRepo extends JpaRepository<SkipCondition, Long> {

}
