package com.mospi.questionare_survey.repository;

import com.mospi.questionare_survey.entity.UserInfoTrail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserInfoTrailRepo extends JpaRepository<UserInfoTrail, Long> {

    // Kisi specific user ka audit history fetch karne ke liye
    List<UserInfoTrail> findByUser_UserIdOrderByChangedAtDesc(Long userId);

    // Kisi specific action type (jaise UPDATE, DELETE) ki trails fetch karne ke liye
    List<UserInfoTrail> findByActionType(String actionType);
}