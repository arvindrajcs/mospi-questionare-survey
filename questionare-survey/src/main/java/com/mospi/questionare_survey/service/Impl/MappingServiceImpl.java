package com.mospi.questionare_survey.service.Impl;


import com.fasterxml.jackson.core.type.TypeReference;
import com.mospi.questionare_survey.entity.*;
import com.mospi.questionare_survey.exception.SDDException;
import com.mospi.questionare_survey.helperUtil.ErrorCode;
import com.mospi.questionare_survey.helperUtil.HelperUtils;
import com.mospi.questionare_survey.helperUtil.ResponseUtils;
import com.mospi.questionare_survey.repository.*;
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

    @Autowired
    private UserInfoRepo userInfoRepo;

    @Autowired
    private MappingUserRoleRepo mappingUserRoleRepo;


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
        try {
            if (request.getUserId() == null) {
                throw new SDDException(false, "User ID cannot be null");
            }
            if (request.getRoleId() == null || request.getRoleId().isEmpty()) {
                throw new SDDException(false, "Role ID list cannot be empty");
            }
            UserInfo user = userInfoRepo.findById(request.getUserId())
                    .orElseThrow(() -> new SDDException(false, "User not found"));

            for (Long rollId : request.getRoleId()) {
                MasRole roll = masRoleRepo.findById(rollId)
                        .orElseThrow(() -> new SDDException(false, "Roll not found for ID : " + rollId));

                boolean alreadyMapped = mappingUserRoleRepo.existsByUserAndRole(user, roll);
                if (alreadyMapped) {
                    continue; // skip already mapped menu
                }
                MappingUserRole obj = new MappingUserRole();
                obj.setUser(user);
                obj.setRole(roll);
                obj.setCreatedBy(userId);
                mappingUserRoleRepo.save(obj);
            }
            DefaultResponse response = new DefaultResponse();
            response.setMsg("User Role Mapping created successfully");
            return ResponseUtils.createSuccessResponse(response, new TypeReference<DefaultResponse>() {});
        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while creating User Role Mapping", e);
            throw new SDDException(false, "Unexpected error during User Role Mapping creation");
        }
    }

    @Override
    public ApiResponse<DefaultResponse> updateUserRollStatus(Long userId, UserRoleStatusReq request) {
        try {

            MappingUserRole existingRole = mappingUserRoleRepo.findById(request.getUserRoleMappingId())
                    .orElseThrow(() -> new SDDException(false, ErrorCode.AU1002));

            existingRole.setActive(request.isActive());
            existingRole.setUpdatedBy(userId);
            mappingUserRoleRepo.save(existingRole);

            String message = request.isActive() ? "User-Role Mapped activated successfully" : "User-Role Mapped deactivated successfully";
            logger.info("User-Role Mapped status updated successfully for RollId: {}", existingRole.getRole().getRoleId());
            DefaultResponse defaultResponse = new DefaultResponse(message);
            return ResponseUtils.createSuccessResponse(defaultResponse);

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating User-Role Mapped status for userId: {}", userId, e);
            throw new SDDException(false, "Unexpected error during User-Role Mapped status updating");
        }
    }

    @Override
    public ApiResponseWithPageable<List<UserRoleResp>> getAllUserRoll(GetUserRoleReq request) {
        logger.info("Fetching User-Role Mappings. UserId: {}, Page: {}, Limit: {}",
                request.getUserId(), request.getPage(), request.getLimit());

        try {
            Pageable pageable = helperUtill.getPageable(request.getPage(), request.getLimit());
            List<UserRoleResp> userRoleRespList = new ArrayList<>();
            long totalElements = 0;
            int totalPages = 0;

            if (request.getUserId() != null) {
                // Scenario 1: If userId is provided, fetch only that specific user
                UserInfo user = userInfoRepo.findById(request.getUserId())
                        .orElseThrow(() -> new SDDException(false, "User not found for ID: " + request.getUserId()));

                userRoleRespList.add(buildUserRoleResp(user));
                totalElements = 1;
                totalPages = 1;

            } else {
                // Scenario 2: If userId is NOT provided, paginate through all users and group their roles
                Page<UserInfo> userPage = userInfoRepo.findAll(pageable);
                totalElements = userPage.getTotalElements();
                totalPages = userPage.getTotalPages();

                for (UserInfo user : userPage.getContent()) {
                    userRoleRespList.add(buildUserRoleResp(user));
                }
            }

            logger.info("User-Role details fetched successfully");
            return ResponseUtils.createPageableSuccessResponse(
                    userRoleRespList,
                    totalElements,
                    totalPages,
                    pageable.getPageNumber() + 1,
                    pageable.getPageSize()
            );

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching user-role details", e);
            throw new SDDException(false, "Unexpected error during fetching user-role details");
        }
    }


    private UserRoleResp buildUserRoleResp(UserInfo user) {
        UserRoleResp resp = new UserRoleResp();
        resp.setUserId(user.getUserId()); // Ensure your UserDetails entity has getUserId() and getUsername()
        resp.setUsername(user.getUsername());

        // Fetch mappings for the given user (Assumes you have a repository like mappingUserRoleRepo or userRoleRepo)
        List<MappingUserRole> mappings = mappingUserRoleRepo.findByUser(user);

        List<RollResponse> rollList = mappings.stream().map(mapping -> {
            MasRole role = mapping.getRole(); // Assumes mapping links User to MasRole
            RollResponse rollResp = new RollResponse();
            rollResp.setRoleId(role.getRoleId());
            rollResp.setRoleName(role.getRoleName());
            rollResp.setRoleDescription(role.getRoleDescription()); // Ensure MasRole has getRoleDescription()
            return rollResp;
        }).collect(Collectors.toList());

        resp.setRollList(rollList);
        return resp;
    }




}
