package com.mospi.questionare_survey.service.Impl;


import com.fasterxml.jackson.core.type.TypeReference;
import com.mospi.questionare_survey.entity.*;
import com.mospi.questionare_survey.exception.SDDException;
import com.mospi.questionare_survey.helperUtil.ErrorCode;
import com.mospi.questionare_survey.helperUtil.HelperUtils;
import com.mospi.questionare_survey.helperUtil.ResponseUtils;
import com.mospi.questionare_survey.repository.*;
import com.mospi.questionare_survey.request.*;
import com.mospi.questionare_survey.response.ApiResponse;
import com.mospi.questionare_survey.response.ApiResponseWithPageable;
import com.mospi.questionare_survey.response.DefaultResponse;
import com.mospi.questionare_survey.service.MasterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MasterServiceImpl implements MasterService {

    private final Logger logger = LoggerFactory.getLogger(MasterServiceImpl.class);

    @Autowired
    private HelperUtils helperUtill;

    @Autowired
    private MasMenuRepo masMenuRepo;

    @Autowired
    private MasRoleRepo masRoleRepo;

    @Autowired
    private MasScheduleTypeRepo masScheduleTypeRepo;

    @Autowired
    private MasSurveyTypeRepo masSurveyTypeRepo;

    @Autowired
    private MasDivisionRepo masDivisionRepo;



    //**********************MAS_MENU******************************/

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<DefaultResponse> createMenu(Long userId, CreateMenuReq request) {
        try {
            MasMenu menu = MasMenu.builder()
                    .parentId(request.getParentId())
                    .childId(request.getChildId())
                    .menuUrl(request.getMenuUrl())
                    .iconName(request.getIconName())
                    .labelName(request.getLabelName())
                    .menuType(request.getMenuType())
                    .menuOrder(request.getMenuOrder() != null ? request.getMenuOrder() : 1)
                    .isActive(true)
                    .createdBy(userId)
                    .updatedBy(userId)
                    .build();
            masMenuRepo.save(menu);
            logger.info("Menu created successfully by userId: {}", userId);
            DefaultResponse response = new DefaultResponse();
            response.setMsg("Menu created successfully");
            return ResponseUtils.createSuccessResponse(response);
        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while creating menu for userId: {}", userId, e);
            throw new SDDException(false, "Unexpected error during menu creation");
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<DefaultResponse> updateMenu(Long userId, CreateMenuReq request) {
        try {
            // 1. Fetch existing menu or throw custom exception if not found
            MasMenu existingMenu = masMenuRepo.findById(request.getMenuId())
                    .orElseThrow(() -> new SDDException(false, ErrorCode.AU1002));

            // 2. Update fields from the request DTO
            existingMenu.setParentId(request.getParentId());
            existingMenu.setChildId(request.getChildId());
            existingMenu.setMenuUrl(request.getMenuUrl());
            existingMenu.setIconName(request.getIconName());
            existingMenu.setLabelName(request.getLabelName());
            existingMenu.setMenuType(request.getMenuType());
            if (request.getMenuOrder() != null) {
                existingMenu.setMenuOrder(request.getMenuOrder());
            }
            existingMenu.setUpdatedBy(userId);
            masMenuRepo.save(existingMenu);
            logger.info("Menu updated successfully by userId: {}", userId);
            DefaultResponse defaultResponse = new DefaultResponse("Menu updated successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse);

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating menu for userId: {}", userId, e);
            throw new SDDException(false, "Unexpected error during menu updating");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<DefaultResponse> updateMenuStatus(Long userId, MenuStatusReq request) {
        try {

            MasMenu existingMenu = masMenuRepo.findById(request.getMenuId())
                    .orElseThrow(() -> new SDDException(false, ErrorCode.AU1002));

            existingMenu.setActive(request.isActive());
            existingMenu.setUpdatedBy(userId);
            masMenuRepo.save(existingMenu);

            String message = request.isActive() ? "Menu activated successfully" : "Menu deactivated successfully";
            logger.info("Menu status updated successfully by userId: {}", userId);
            DefaultResponse defaultResponse = new DefaultResponse(message);
            return ResponseUtils.createSuccessResponse(defaultResponse);

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating menu status for userId: {}", userId, e);
            throw new SDDException(false, "Unexpected error during menu status updating");
        }
    }

    @Override
    public ApiResponse<MasMenu> getMenuByMenuId(GetMenuReq request) {
        try {

            MasMenu menu = masMenuRepo.findById(request.getMenuId())
                    .orElseThrow(() -> new SDDException(false, ErrorCode.AU1002));
            logger.info("Menu fetching successfully by menuId: {}", request.getMenuId());
            return ResponseUtils.createSuccessResponse(
                    menu, new TypeReference<MasMenu>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching menu details by menuId: {}", request.getMenuId(), e);
            throw new SDDException(false, "Unexpected error during menu details fetching by menuId");
        }
    }


    public ApiResponseWithPageable<List<MasMenu>> getAllMenus(GetMenuReq request) {

        logger.info("Fetching Menus. Page: {}, Limit: {}", request.getPage(), request.getLimit());

        try {
            Pageable pageable = helperUtill.getPageable(request.getPage(), request.getLimit());
            Page<MasMenu> menusPage = masMenuRepo.findAll(pageable);
            logger.info("Menu Details fetching successfully ");
            return ResponseUtils.createPageableSuccessResponse(
                    menusPage.getContent(),
                    menusPage.getTotalElements(),
                    menusPage.getTotalPages(),
                    pageable.getPageNumber() + 1,
                    pageable.getPageSize()
            );

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching menu details ", e);
            throw new SDDException(false, "Unexpected error during fetching menu details ");
        }
    }



    //**********************MAS_ROLE******************************/


    @Override
    public ApiResponse<DefaultResponse> createRole(Long userId, CreateRoleReq request) {
        try {
            MasRole role = MasRole.builder()
                    .roleName(request.getRoleName())
                    .roleDescription(request.getDescription())
                    .isActive(true)
                    .createdBy(userId)
                    .build();
            masRoleRepo.save(role);
            logger.info("Role created successfully by userId: {}", userId);
            DefaultResponse response = new DefaultResponse();
            response.setMsg("Role created successfully");
            return ResponseUtils.createSuccessResponse(response);
        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while creating Role for userId: {}", userId, e);
            throw new SDDException(false, "Unexpected error during Role creation");
        }
    }

    @Override
    public ApiResponse<DefaultResponse> updateRole(Long userId, CreateRoleReq request) {
        try {
            // 1. Fetch existing Role or throw custom exception if not found
            MasRole existingRole = masRoleRepo.findById(request.getRoleId())
                    .orElseThrow(() -> new SDDException(false, ErrorCode.AU1002));

            // 2. Update fields from the request DTO
            existingRole.setRoleName(request.getRoleName());
            existingRole.setRoleDescription(request.getDescription());
            existingRole.setUpdatedBy(userId);
            masRoleRepo.save(existingRole);
            logger.info("Role updated successfully by userId: {}", userId);
            DefaultResponse defaultResponse = new DefaultResponse("Role updated successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse);

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating Role for userId: {}", userId, e);
            throw new SDDException(false, "Unexpected error during Role updating");
        }
    }

    @Override
    public ApiResponse<DefaultResponse> updateRoleStatus(Long userId, RoleStatusReq request) {
        try {

            MasRole existingRole = masRoleRepo.findById(request.getRoleId())
                    .orElseThrow(() -> new SDDException(false, ErrorCode.AU1002));

            existingRole.setActive(request.isActive());
            existingRole.setUpdatedBy(userId);
            masRoleRepo.save(existingRole);

            String message = request.isActive() ? "Role activated successfully" : "Role deactivated successfully";
            logger.info("Role status updated successfully by userId: {}", userId);
            DefaultResponse defaultResponse = new DefaultResponse(message);
            return ResponseUtils.createSuccessResponse(defaultResponse);

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating Role status for userId: {}", userId, e);
            throw new SDDException(false, "Unexpected error during Role status updating");
        }
    }

    @Override
    public ApiResponse<MasRole> getRoleByRoleId(GetRoleReq request) {
        try {

            MasRole role = masRoleRepo.findById(request.getRoleId())
                    .orElseThrow(() -> new SDDException(false, ErrorCode.AU1002));
            logger.info("Role fetching successfully by roleId: {}", request.getRoleId());
            return ResponseUtils.createSuccessResponse(
                    role, new TypeReference<MasRole>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching Role details by roleId: {}", request.getRoleId(), e);
            throw new SDDException(false, "Unexpected error during Role details fetching by roleId");
        }
    }

    @Override
    public ApiResponseWithPageable<List<MasRole>> getAllRole(GetRoleReq request) {
        logger.info("Fetching Role. Page: {}, Limit: {}", request.getPage(), request.getLimit());

        try {
            Pageable pageable = helperUtill.getPageable(request.getPage(), request.getLimit());
            Page<MasRole> rolesPage = masRoleRepo.findAll(pageable);
            logger.info("Role Details fetching successfully ");
            return ResponseUtils.createPageableSuccessResponse(
                    rolesPage.getContent(),
                    rolesPage.getTotalElements(),
                    rolesPage.getTotalPages(),
                    pageable.getPageNumber() + 1,
                    pageable.getPageSize()
            );

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching Role details ", e);
            throw new SDDException(false, "Unexpected error during fetching Role details ");
        }
    }


    //**********************MAS_SCHEDULE_TYPE******************************/


    @Override
    public ApiResponse<DefaultResponse> createScheduleType(Long userId, CreateScheduleTypeReq request) {
        try {
            MasScheduleType schedule = MasScheduleType.builder()
                    .description(request.getDescription())
                    .isActive(true)
                    .createdBy(userId)
                    .build();
            masScheduleTypeRepo.save(schedule);
            logger.info("Schedule Type created successfully by userId: {}", userId);
            DefaultResponse response = new DefaultResponse();
            response.setMsg("Schedule Type created successfully");
            return ResponseUtils.createSuccessResponse(response);
        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while creating Schedule Type for userId: {}", userId, e);
            throw new SDDException(false, "Unexpected error during Schedule Type creation");
        }
    }

    @Override
    public ApiResponse<DefaultResponse> updateScheduleType(Long userId, CreateScheduleTypeReq request) {
        try {
            // 1. Fetch existing Schedule Type or throw custom exception if not found
            MasScheduleType existingScheduleType = masScheduleTypeRepo.findById(request.getScheduleTypeId())
                    .orElseThrow(() -> new SDDException(false, ErrorCode.AU1002));

            // 2. Update fields from the request DTO
            existingScheduleType.setDescription(request.getDescription());
            existingScheduleType.setUpdatedBy(userId);
            masScheduleTypeRepo.save(existingScheduleType);
            logger.info("Schedule Type updated successfully by userId: {}", userId);
            DefaultResponse defaultResponse = new DefaultResponse("Schedule Type updated successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse);

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating Schedule Type for userId: {}", userId, e);
            throw new SDDException(false, "Unexpected error during Schedule Type updating");
        }
    }

    @Override
    public ApiResponse<DefaultResponse> updateScheduleTypeStatus(Long userId, ScheduleTypeStatusReq request) {
        try {

            MasScheduleType existingScheduleType = masScheduleTypeRepo.findById(request.getScheduleTypeId())
                    .orElseThrow(() -> new SDDException(false, ErrorCode.AU1002));

            existingScheduleType.setActive(request.isActive());
            existingScheduleType.setUpdatedBy(userId);
            masScheduleTypeRepo.save(existingScheduleType);

            String message = request.isActive() ? "Schedule Type activated successfully" : "Schedule Type deactivated successfully";
            logger.info("Schedule Type status updated successfully by userId: {}", userId);
            DefaultResponse defaultResponse = new DefaultResponse(message);
            return ResponseUtils.createSuccessResponse(defaultResponse);

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating Schedule Type status for userId: {}", userId, e);
            throw new SDDException(false, "Unexpected error during Schedule Type status updating");
        }
    }

    @Override
    public ApiResponse<MasScheduleType> getScheduleTypeByMenuId(GetScheduleTypeReq request) {
        try {

            MasScheduleType scheduleType = masScheduleTypeRepo.findById(request.getScheduleTypeId())
                    .orElseThrow(() -> new SDDException(false, ErrorCode.AU1002));
            logger.info("Schedule Type fetching successfully by scheduleTypeId: {}", request.getScheduleTypeId());
            return ResponseUtils.createSuccessResponse(
                    scheduleType, new TypeReference<MasScheduleType>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching Schedule Type details by scheduleTypeId: {}", request.getScheduleTypeId(), e);
            throw new SDDException(false, "Unexpected error during Schedule Type details fetching by scheduleTypeId");
        }
    }

    @Override
    public ApiResponseWithPageable<List<MasScheduleType>> getAllScheduleType(GetScheduleTypeReq request) {
        try {
            Pageable pageable = helperUtill.getPageable(request.getPage(), request.getLimit());
            Page<MasScheduleType> scheduleTypePage = masScheduleTypeRepo.findAll(pageable);
            logger.info("schedule Type Details fetching successfully ");
            return ResponseUtils.createPageableSuccessResponse(
                    scheduleTypePage.getContent(),
                    scheduleTypePage.getTotalElements(),
                    scheduleTypePage.getTotalPages(),
                    pageable.getPageNumber() + 1,
                    pageable.getPageSize()
            );

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching schedule Type details ", e);
            throw new SDDException(false, "Unexpected error during fetching schedule Type details ");
        }
    }


    //**********************MAS_SURVEY_TYPE******************************/


    @Override
    public ApiResponse<DefaultResponse> createSurveyType(Long userId, CreateSurveyTypeReq request) {
        try {
            MasSurveyType survey = MasSurveyType.builder()
                    .description(request.getDescription())
                    .isActive(true)
                    .createdBy(userId)
                    .build();
            masSurveyTypeRepo.save(survey);
            logger.info("Survey Type created successfully by userId: {}", userId);
            DefaultResponse response = new DefaultResponse();
            response.setMsg("Survey Type created successfully");
            return ResponseUtils.createSuccessResponse(response);
        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while creating Survey Type for userId: {}", userId, e);
            throw new SDDException(false, "Unexpected error during Survey Type creation");
        }
    }

    @Override
    public ApiResponse<DefaultResponse> updateSurveyType(Long userId, CreateSurveyTypeReq request) {
        try {
            // 1. Fetch existing Survey Type or throw custom exception if not found
            MasSurveyType existingSurveyType = masSurveyTypeRepo.findById(request.getSurveyTypeId())
                    .orElseThrow(() -> new SDDException(false, ErrorCode.AU1002));

            // 2. Update fields from the request DTO
            existingSurveyType.setDescription(request.getDescription());
            existingSurveyType.setUpdatedBy(userId);
            masSurveyTypeRepo.save(existingSurveyType);
            logger.info("Survey Type updated successfully by userId: {}", userId);
            DefaultResponse defaultResponse = new DefaultResponse("Survey Type updated successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse);

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating Survey Type for userId: {}", userId, e);
            throw new SDDException(false, "Unexpected error during Survey Type updating");
        }
    }

    @Override
    public ApiResponse<DefaultResponse> updateSurveyTypeStatus(Long userId, SurveyTypeStatusReq request) {
        try {

            MasSurveyType existingSurveyType = masSurveyTypeRepo.findById(request.getSurveyTypeId())
                    .orElseThrow(() -> new SDDException(false, ErrorCode.AU1002));

            existingSurveyType.setActive(request.isActive());
            existingSurveyType.setUpdatedBy(userId);
            masSurveyTypeRepo.save(existingSurveyType);

            String message = request.isActive() ? "Survey Type activated successfully" : "Survey Type deactivated successfully";
            logger.info("Survey Type status updated successfully by userId: {}", userId);
            DefaultResponse defaultResponse = new DefaultResponse(message);
            return ResponseUtils.createSuccessResponse(defaultResponse);

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating Survey Type status for userId: {}", userId, e);
            throw new SDDException(false, "Unexpected error during Survey Type status updating");
        }
    }

    @Override
    public ApiResponse<MasSurveyType> getSurveyTypeByMenuId(GetSurveyTypeReq request) {
        try {

            MasSurveyType surveyType = masSurveyTypeRepo.findById(request.getSurveyTypeId())
                    .orElseThrow(() -> new SDDException(false, ErrorCode.AU1002));
            logger.info("Survey Type fetching successfully by SurveyTypeId: {}", request.getSurveyTypeId());
            return ResponseUtils.createSuccessResponse(
                    surveyType, new TypeReference<MasSurveyType>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching Survey Type details by SurveyTypeId: {}", request.getSurveyTypeId(), e);
            throw new SDDException(false, "Unexpected error during Survey Type details fetching by SurveyTypeId");
        }
    }

    @Override
    public ApiResponseWithPageable<List<MasSurveyType>> getAllSurveyType(GetSurveyTypeReq request) {
        try {
            Pageable pageable = helperUtill.getPageable(request.getPage(), request.getLimit());
            Page<MasSurveyType> surveyTypePage = masSurveyTypeRepo.findAll(pageable);
            logger.info("survey Type Details fetching successfully ");
            return ResponseUtils.createPageableSuccessResponse(
                    surveyTypePage.getContent(),
                    surveyTypePage.getTotalElements(),
                    surveyTypePage.getTotalPages(),
                    pageable.getPageNumber() + 1,
                    pageable.getPageSize()
            );

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching survey Type details ", e);
            throw new SDDException(false, "Unexpected error during fetching survey Type details ");
        }
    }


    //**********************MAS_DIVISION******************************/


    @Override
    public ApiResponse<DefaultResponse> createDivision(Long userId, CreateDivisionReq request) {
        try {
            MasDivision division = MasDivision.builder()
                    .description(request.getDescription())
                    .isActive(true)
                    .createdBy(userId)
                    .build();
            masDivisionRepo.save(division);
            logger.info("Division created successfully by userId: {}", userId);
            DefaultResponse response = new DefaultResponse();
            response.setMsg("Division created successfully");
            return ResponseUtils.createSuccessResponse(response);
        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while creating Division for userId: {}", userId, e);
            throw new SDDException(false, "Unexpected error during Division creation");
        }
    }

    @Override
    public ApiResponse<DefaultResponse> updateDivision(Long userId, CreateDivisionReq request) {
        try {
            // 1. Fetch existing Division or throw custom exception if not found
            MasDivision existingDivision = masDivisionRepo.findById(request.getDivisionId())
                    .orElseThrow(() -> new SDDException(false, ErrorCode.AU1002));

            // 2. Update fields from the request DTO
            existingDivision.setDescription(request.getDescription());
            existingDivision.setUpdatedBy(userId);
            masDivisionRepo.save(existingDivision);
            logger.info("Division updated successfully by userId: {}", userId);
            DefaultResponse defaultResponse = new DefaultResponse("Division updated successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse);

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating Division for userId: {}", userId, e);
            throw new SDDException(false, "Unexpected error during Division updating");
        }
    }

    @Override
    public ApiResponse<DefaultResponse> updateDivisionStatus(Long userId, DivisionStatusReq request) {
        try {

            MasDivision existingDivision = masDivisionRepo.findById(request.getDivisionId())
                    .orElseThrow(() -> new SDDException(false, ErrorCode.AU1002));

            existingDivision.setActive(request.isActive());
            existingDivision.setUpdatedBy(userId);
            masDivisionRepo.save(existingDivision);

            String message = request.isActive() ? "Division activated successfully" : "Division deactivated successfully";
            logger.info("Division status updated successfully by userId: {}", userId);
            DefaultResponse defaultResponse = new DefaultResponse(message);
            return ResponseUtils.createSuccessResponse(defaultResponse);

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating Division status for userId: {}", userId, e);
            throw new SDDException(false, "Unexpected error during Division status updating");
        }
    }

    @Override
    public ApiResponse<MasDivision> getDivisionByMenuId(GetDivisionReq request) {
        try {

            MasDivision division = masDivisionRepo.findById(request.getDivisionId())
                    .orElseThrow(() -> new SDDException(false, ErrorCode.AU1002));
            logger.info("Division fetching successfully by DivisionId: {}", request.getDivisionId());
            return ResponseUtils.createSuccessResponse(
                    division, new TypeReference<MasDivision>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching Division details by DivisionId: {}", request.getDivisionId(), e);
            throw new SDDException(false, "Unexpected error during Division details fetching by DivisionId");
        }
    }

    @Override
    public ApiResponseWithPageable<List<MasDivision>> getAllDivision(GetDivisionReq request) {
        try {
            Pageable pageable = helperUtill.getPageable(request.getPage(), request.getLimit());
            Page<MasDivision> divisionPage = masDivisionRepo.findAll(pageable);
            logger.info("Division Details fetching successfully ");
            return ResponseUtils.createPageableSuccessResponse(
                    divisionPage.getContent(),
                    divisionPage.getTotalElements(),
                    divisionPage.getTotalPages(),
                    pageable.getPageNumber() + 1,
                    pageable.getPageSize()
            );

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching Division details ", e);
            throw new SDDException(false, "Unexpected error during fetching Division details ");
        }
    }







}
