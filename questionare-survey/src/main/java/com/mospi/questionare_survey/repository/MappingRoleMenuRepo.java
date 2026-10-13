package com.mospi.questionare_survey.repository;

import com.mospi.questionare_survey.entity.MappingRoleMenu;
import com.mospi.questionare_survey.entity.MasMenu;
import com.mospi.questionare_survey.entity.MasRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MappingRoleMenuRepo extends JpaRepository<MappingRoleMenu, Long> {

    // Kisi specific role ke saare mapped menus fetch karne ke liye
    List<MappingRoleMenu> findByRole_RoleId(Long roleId);

    // Kisi specific menu ke saare mapped roles fetch karne ke liye
    List<MappingRoleMenu> findByMenu_MenuId(Long menuId);

    // Check karne ke liye ki role ke paas menu assigned hai ya nahi
    boolean existsByRole_RoleIdAndMenu_MenuId(Long roleId, Long menuId);

    // Specific role aur menu ki mapping find karne ke liye
    Optional<MappingRoleMenu> findByRole_RoleIdAndMenu_MenuId(Long roleId, Long menuId);

    boolean existsByRoleAndMenu(MasRole role, MasMenu menu);

    List<MappingRoleMenu> findByRole(MasRole role);
}