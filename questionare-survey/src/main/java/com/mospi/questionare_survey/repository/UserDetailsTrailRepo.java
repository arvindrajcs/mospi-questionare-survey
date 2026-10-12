package com.mospi.questionare_survey.repository;

import com.mospi.questionare_survey.entity.UserDetailsTrail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDetailsTrailRepo extends JpaRepository<UserDetailsTrail, Long> {

    // Kisi specific user ka audit history fetch karne ke liye
    List<UserDetailsTrail> findByUser_UserIdOrderByChangedAtDesc(Long userId);

    // Kisi specific action type (jaise UPDATE, DELETE) ki trails fetch karne ke liye
    List<UserDetailsTrail> findByActionType(String actionType);
}