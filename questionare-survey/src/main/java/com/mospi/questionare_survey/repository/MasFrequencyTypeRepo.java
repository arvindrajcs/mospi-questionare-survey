package com.mospi.questionare_survey.repository;

import com.mospi.questionare_survey.entity.MasFrequencyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MasFrequencyTypeRepo extends JpaRepository<MasFrequencyType, Long> {
}
