package com.mospi.questionare_survey.repository;

import com.mospi.questionare_survey.entity.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDetailsRepo extends JpaRepository<UserDetails, Long> {

    Optional<UserDetails> findByUsername(String username);

    Optional<UserDetails> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}