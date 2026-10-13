package com.mospi.questionare_survey.service.Impl;


import com.fasterxml.jackson.core.type.TypeReference;
import com.mospi.questionare_survey.entity.MappingRoleMenu;
import com.mospi.questionare_survey.entity.MasMenu;
import com.mospi.questionare_survey.entity.MasRole;
import com.mospi.questionare_survey.exception.SDDException;
import com.mospi.questionare_survey.helperUtil.ErrorCode;
import com.mospi.questionare_survey.helperUtil.HelperUtils;
import com.mospi.questionare_survey.helperUtil.ResponseUtils;
import com.mospi.questionare_survey.repository.MappingRoleMenuRepo;
import com.mospi.questionare_survey.repository.MasMenuRepo;
import com.mospi.questionare_survey.repository.MasRoleRepo;
import com.mospi.questionare_survey.request.*;
import com.mospi.questionare_survey.response.*;
import com.mospi.questionare_survey.service.MappingService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class MappingServiceImpl implements MappingService {

    private final Logger logger = LoggerFactory.getLogger(MappingServiceImpl.class);

    @Autowired
    private HelperUtils helperUtill;

    @Autowired
    private MasMenuRepo masMenuRepo;

    @Autowired
    private MasRoleRepo masRoleRepo;

    @Autowired
    private MappingRoleMenuRepo mappingRoleMenuRepo;


    //**********************MAPPING ROLE-MENU******************************/

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<DefaultResponse> createRoleMenuMapping(Long userId, RoleMenuReq request) {

        try {
            if (request.getRoleId() == null) {
                throw new SDDException(false, "Role ID cannot be null");
            }
            if (request.getMenuId() == null || request.getMenuId().isEmpty()) {
                throw new SDDException(false, "Menu ID list cannot be empty");
            }
            MasRole role = masRoleRepo.findById(request.getRoleId())
                    .orElseThrow(() -> new SDDException(false, "Role not found"));

            for (Long menuId : request.getMenuId()) {
                MasMenu menu = masMenuRepo.findById(menuId)
                        .orElseThrow(() -> new SDDException(false, "Menu not found for ID : " + menuId));

                boolean alreadyMapped = mappingRoleMenuRepo.existsByRoleAndMenu(role, menu);
                if (alreadyMapped) {
                    continue; // skip already mapped menu
                }
                MappingRoleMenu obj = new MappingRoleMenu();
                obj.setRole(role);
                obj.setMenu(menu);
                obj.setCreatedBy(userId);
                mappingRoleMenuRepo.save(obj);
            }
            DefaultResponse response = new DefaultResponse();
            response.setMsg("Role Menu Mapping created successfully");
            return ResponseUtils.createSuccessResponse(response, new TypeReference<DefaultResponse>() {});
        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while creating Role Menu Mapping", e);
            throw new SDDException(false, "Unexpected error during Role Menu Mapping creation");
        }
    }

    @Override
    public ApiResponse<DefaultResponse> updateRoleMenuStatus(Long userId, RoleMenuStatusReq request) {
        try {

            MappingRoleMenu existingMenu = mappingRoleMenuRepo.findById(request.getRoleMenuMappingId())
                    .orElseThrow(() -> new SDDException(false, ErrorCode.AU1002));

            existingMenu.setActive(request.isActive());
            existingMenu.setUpdatedBy(userId);
            mappingRoleMenuRepo.save(existingMenu);

            String message = request.isActive() ? "Role-Menu Mapped activated successfully" : "Role-Menu Mapped deactivated successfully";
            logger.info("Role-Menu Mapped status updated successfully for MenuId: {}", existingMenu.getMenu().getMenuId());
            DefaultResponse defaultResponse = new DefaultResponse(message);
            return ResponseUtils.createSuccessResponse(defaultResponse);

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating Role-Menu Mapped status for userId: {}", userId, e);
            throw new SDDException(false, "Unexpected error during Role-Menu Mapped status updating");
        }
    }

    @Override
    public ApiResponseWithPageable<List<RoleMenuResp>> getAllRoleMenusWithPage(GetRoleMenuReq request) {

        logger.info("Fetching Role-Menu Mappings. RoleId: {}, Page: {}, Limit: {}",
                request.getRoleId(), request.getPage(), request.getLimit());

        try {
            Pageable pageable = helperUtill.getPageable(request.getPage(), request.getLimit());
            List<RoleMenuResp> roleMenuRespList = new ArrayList<>();
            long totalElements = 0;
            int totalPages = 0;

            if (request.getRoleId() != null) {
                // Scenario 1: If roleId is provided, fetch only that specific role
                MasRole role = masRoleRepo.findById(request.getRoleId())
                        .orElseThrow(() -> new SDDException(false, "Role not found for ID: " + request.getRoleId()));

                roleMenuRespList.add(buildRoleMenuResp(role));
                totalElements = 1;
                totalPages = 1;

            } else {
                // Scenario 2: If roleId is NOT provided, paginate through all roles and group their menus
                Page<MasRole> rolePage = masRoleRepo.findAll(pageable);
                totalElements = rolePage.getTotalElements();
                totalPages = rolePage.getTotalPages();

                for (MasRole role : rolePage.getContent()) {
                    roleMenuRespList.add(buildRoleMenuResp(role));
                }
            }
            logger.info("Role-Menu details fetched successfully");
            return ResponseUtils.createPageableSuccessResponse(
                    roleMenuRespList,
                    totalElements,
                    totalPages,
                    pageable.getPageNumber() + 1,
                    pageable.getPageSize()
            );

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching role-menu details", e);
            throw new SDDException(false, "Unexpected error during fetching role-menu details");
        }
    }


    private RoleMenuResp buildRoleMenuResp(MasRole role) {
        RoleMenuResp resp = new RoleMenuResp();
        resp.setRoleId(role.getRoleId());
        resp.setRoleName(role.getRoleName());

        // Fetch mappings for the given role
        List<MappingRoleMenu> mappings = mappingRoleMenuRepo.findByRole(role);

        List<MenuResponse> menuList = mappings.stream().map(mapping -> {
            MasMenu menu = mapping.getMenu();
            MenuResponse menuResp = new MenuResponse();
            menuResp.setMenuId(menu.getMenuId());
            menuResp.setParentId(menu.getParentId());
            menuResp.setChildId(menu.getChildId());
            menuResp.setMenuUrl(menu.getMenuUrl());
            menuResp.setIconName(menu.getIconName());
            menuResp.setLabelName(menu.getLabelName());
            return menuResp;
        }).collect(Collectors.toList());

        resp.setMenuList(menuList);
        return resp;
    }



    //**********************MAPPING USER-ROLE******************************/


    @Override
    public ApiResponse<DefaultResponse> createUserRoll(Long userId, UserRoleReq request) {
        return null;
    }

    @Override
    public ApiResponse<DefaultResponse> updateUserRollStatus(Long userId, UserRoleStatusReq request) {
        return null;
    }

    @Override
    public ApiResponseWithPageable<List<UserRoleResp>> getAllUserRoll(GetUserRoleReq request) {
        return null;
    }



}
