package com.mospi.questionare_survey.repository;

import com.mospi.questionare_survey.entity.MasRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MasRoleRepo extends JpaRepository<MasRole, Long> {

    Optional<MasRole> findByRoleName(String roleName);

    boolean existsByRoleName(String roleName);
}