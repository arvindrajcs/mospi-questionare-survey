package com.mospi.questionare_survey.repository;

import com.mospi.questionare_survey.entity.MasMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MasMenuRepo extends JpaRepository<MasMenu, Long> {

    // Fetch all top-level parent menus where parentId is null or empty
    List<MasMenu> findByParentIdIsNullOrderByMenuOrderAsc();

    // Fetch sub-menus by parentId string matching
    List<MasMenu> findByParentIdOrderByMenuOrderAsc(String parentId);

    // Fetch all active menus
    List<MasMenu> findByIsActiveTrueOrderByMenuOrderAsc();
}