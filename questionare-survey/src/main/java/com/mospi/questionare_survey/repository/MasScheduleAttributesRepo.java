package com.mospi.questionare_survey.repository;

import com.mospi.questionare_survey.entity.MasScheduleAttributes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MasScheduleAttributesRepo extends JpaRepository<MasScheduleAttributes,Long> {

}
