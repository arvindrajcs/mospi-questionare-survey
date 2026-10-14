package com.mospi.questionare_survey.repository;

import com.mospi.questionare_survey.entity.MappingUserRole;
import com.mospi.questionare_survey.entity.MasRole;
import com.mospi.questionare_survey.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MappingUserRoleRepo extends JpaRepository<MappingUserRole, Long> {

    boolean existsByUserAndRole(UserInfo user, MasRole roll);

    List<MappingUserRole> findByUser(UserInfo user);

    @Query("SELECT r.roleName FROM MappingUserRole m JOIN m.role r WHERE m.user.userId = :userId AND m.isActive = true")
    List<String> findRoleNamesByUserId(@Param("userId") Long userId);
}