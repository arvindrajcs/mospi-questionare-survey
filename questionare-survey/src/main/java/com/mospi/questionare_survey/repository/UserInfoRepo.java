package com.mospi.questionare_survey.repository;

import com.mospi.questionare_survey.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserInfoRepo extends JpaRepository<UserInfo, Long> {

    Optional<UserInfo> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    UserInfo  findByUsernameIgnoreCaseAndIsActive(String username,boolean isActive);

    UserInfo findByUsername(String username);
}