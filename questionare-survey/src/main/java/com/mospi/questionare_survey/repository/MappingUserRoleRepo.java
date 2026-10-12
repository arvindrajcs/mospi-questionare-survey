package com.mospi.questionare_survey.repository;

import com.mospi.questionare_survey.entity.MappingUserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MappingUserRoleRepo extends JpaRepository<MappingUserRole, Long> {

    // Kisi specific user ke saare roles fetch karne ke liye
    List<MappingUserRole> findByUser_UserId(Long userId);

    // Kisi specific role ke saare users fetch karne ke liye
    List<MappingUserRole> findByRole_RoleId(Long roleId);

    // Check karne ke liye ki kya user ke paas already wo role assigned hai
    boolean existsByUser_UserIdAndRole_RoleId(Long userId, Long roleId);

    // Specific user aur role ki mapping find karne ke liye
    Optional<MappingUserRole> findByUser_UserIdAndRole_RoleId(Long userId, Long roleId);
}