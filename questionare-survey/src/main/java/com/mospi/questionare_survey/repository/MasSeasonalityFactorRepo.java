package com.mospi.questionare_survey.repository;

import com.mospi.questionare_survey.entity.MasSeasonalityFactor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MasSeasonalityFactorRepo extends JpaRepository<MasSeasonalityFactor,  Long> {
}
