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
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

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

    @Autowired
    private MasEnquiryTypeRepo masEnquiryTypeRepo;

    @Autowired
    private MasFrequencyTypeRepo masFrequencyTypeRepo;

    @Autowired
    private MasSeasonalityFactorRepo masSeasonalityFactorRepo;

    @Autowired
    private MasScheduleAttributesRepo masScheduleAttributesRepo;

    @Autowired
    private MasStatusRepo masStatusRepo;

    @Autowired
    private MasValidationRepo masValidationRepo;

    @Autowired
    private MasComparisonRepo masComparisonRepo;



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

            existingMenu.setActive(request.getActive());
            existingMenu.setUpdatedBy(userId);
            masMenuRepo.save(existingMenu);

            String message = request.getActive() ? "Menu activated successfully" : "Menu deactivated successfully";
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

            existingRole.setActive(request.getActive());
            existingRole.setUpdatedBy(userId);
            masRoleRepo.save(existingRole);

            String message = request.getActive() ? "Role activated successfully" : "Role deactivated successfully";
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

            existingScheduleType.setActive(request.getActive());
            existingScheduleType.setUpdatedBy(userId);
            masScheduleTypeRepo.save(existingScheduleType);

            String message = request.getActive() ? "Schedule Type activated successfully" : "Schedule Type deactivated successfully";
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
            Page<MasScheduleType> scheduleTypePage = masScheduleTypeRepo.findAllByOrderByScheduleTypeIdAsc(pageable);
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

            existingSurveyType.setActive(request.getActive());
            existingSurveyType.setUpdatedBy(userId);
            masSurveyTypeRepo.save(existingSurveyType);

            String message = request.getActive() ? "Survey Type activated successfully" : "Survey Type deactivated successfully";
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

            existingDivision.setActive(request.getActive());
            existingDivision.setUpdatedBy(userId);
            masDivisionRepo.save(existingDivision);

            String message = request.getActive() ? "Division activated successfully" : "Division deactivated successfully";
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


    //**********************MAS_ENQUIRY_TYPE******************************/


    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<DefaultResponse> createEnquiry(Long userId, CreateEnquiryReq request) {
        logger.info("Starting enquiry type creation by user: {}", userId);

        try {
            MasEnquiryType enquiryType = MasEnquiryType.builder()
                    .description(request.getDescription())
                    .isActive(true)
                    .createdBy(userId)
                    .build();

            masEnquiryTypeRepo.save(enquiryType);

            logger.info("Enquiry type created successfully with ID: {}", enquiryType.getEnquiryTypeId());

            DefaultResponse defaultResponse = new DefaultResponse();
            defaultResponse.setMsg("Enquiry type created successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {});

        } catch (Exception e) {
            logger.error("Error while creating enquiry type", e);
            throw new SDDException(false, "Unexpected error during enquiry type creation");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<DefaultResponse> updateEnquiry(Long userId, CreateEnquiryReq request) {
        logger.info("Starting enquiry type update for ID: {} by user: {}", request.getEnquiryTypeId(), userId);

        try {
            MasEnquiryType enquiryType = masEnquiryTypeRepo.findById(request.getEnquiryTypeId())
                    .orElseThrow(() -> new SDDException(false, "Enquiry Type not found for ID: " + request.getEnquiryTypeId()));

            enquiryType.setDescription(request.getDescription());
            enquiryType.setUpdatedBy(userId);

            masEnquiryTypeRepo.save(enquiryType);

            logger.info("Enquiry type updated successfully for ID: {}", request.getEnquiryTypeId());

            DefaultResponse defaultResponse = new DefaultResponse();
            defaultResponse.setMsg("Enquiry type updated successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating enquiry type", e);
            throw new SDDException(false, "Unexpected error during enquiry type update");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<DefaultResponse> updateEnquiryStatus(Long userId, EnquiryStatusReq request) {
        logger.info("Starting enquiry type status update for ID: {} by user: {}", request.getEnquiryTypeId(), userId);

        try {
            MasEnquiryType enquiryType = masEnquiryTypeRepo.findById(request.getEnquiryTypeId())
                    .orElseThrow(() -> new SDDException(false, "Enquiry Type not found for ID: " + request.getEnquiryTypeId()));

            enquiryType.setActive(request.getActive() != null ? request.getActive() : true);
            enquiryType.setUpdatedBy(userId);

            masEnquiryTypeRepo.save(enquiryType);

            logger.info("Enquiry type status updated successfully for ID: {}", request.getEnquiryTypeId());

            DefaultResponse defaultResponse = new DefaultResponse();
            defaultResponse.setMsg("Enquiry type status updated successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating enquiry type status", e);
            throw new SDDException(false, "Unexpected error during enquiry type status update");
        }
    }

    @Override
    public ApiResponse<MasEnquiryType> getEnquiryByMenuId(GetEnquiryReq request) {
        logger.info("Fetching enquiry type for ID: {}", request.getEnquiryTypeId());

        try {
            MasEnquiryType enquiryType = masEnquiryTypeRepo.findById(request.getEnquiryTypeId())
                    .orElseThrow(() -> new SDDException(false, "Enquiry Type not found for ID: " + request.getEnquiryTypeId()));

            return ResponseUtils.createSuccessResponse(enquiryType, new TypeReference<MasEnquiryType>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching enquiry type", e);
            throw new SDDException(false, "Unexpected error during enquiry type retrieval");
        }
    }

    @Override
    public ApiResponseWithPageable<List<MasEnquiryType>> getAllEnquiry(GetEnquiryReq request) {
        try {
            Pageable pageable = helperUtill.getPageable(request.getPage(), request.getLimit());
            Page<MasEnquiryType> enquiryPage = masEnquiryTypeRepo.findAll(pageable);

            logger.info("Enquiry Type details fetched successfully");

            return ResponseUtils.createPageableSuccessResponse(
                    enquiryPage.getContent(),
                    enquiryPage.getTotalElements(),
                    enquiryPage.getTotalPages(),
                    pageable.getPageNumber() + 1,
                    pageable.getPageSize()
            );

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching Enquiry Type details", e);
            throw new SDDException(false, "Unexpected error during fetching Enquiry Type details");
        }
    }



    //**********************MAS_FREQUENCY_TYPE******************************/




    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<DefaultResponse> createFrequency(Long userId, CreateFrequencyReq request) {
        logger.info("Starting frequency type creation by user: {}", userId);

        try {
            MasFrequencyType frequencyType = MasFrequencyType.builder()
                    .description(request.getDescription())
                    .isActive(true)
                    .createdBy(userId)
                    .build();

            masFrequencyTypeRepo.save(frequencyType);

            logger.info("Frequency type created successfully with ID: {}", frequencyType.getFrequencyTypeId());

            DefaultResponse defaultResponse = new DefaultResponse();
            defaultResponse.setMsg("Frequency type created successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {});

        } catch (Exception e) {
            logger.error("Error while creating frequency type", e);
            throw new SDDException(false, "Unexpected error during frequency type creation");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<DefaultResponse> updateFrequency(Long userId, CreateFrequencyReq request) {
        logger.info("Starting frequency type update for ID: {} by user: {}", request.getFrequencyTypeId(), userId);

        try {
            MasFrequencyType frequencyType = masFrequencyTypeRepo.findById(request.getFrequencyTypeId())
                    .orElseThrow(() -> new SDDException(false, "Frequency Type not found for ID: " + request.getFrequencyTypeId()));

            frequencyType.setDescription(request.getDescription());
            frequencyType.setUpdatedBy(userId);

            masFrequencyTypeRepo.save(frequencyType);

            logger.info("Frequency type updated successfully for ID: {}", request.getFrequencyTypeId());

            DefaultResponse defaultResponse = new DefaultResponse();
            defaultResponse.setMsg("Frequency type updated successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating frequency type", e);
            throw new SDDException(false, "Unexpected error during frequency type update");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<DefaultResponse> updateFrequencyStatus(Long userId, FrequencyStatusReq request) {
        logger.info("Starting frequency type status update for ID: {} by user: {}", request.getFrequencyTypeId(), userId);

        try {
            MasFrequencyType frequencyType = masFrequencyTypeRepo.findById(request.getFrequencyTypeId())
                    .orElseThrow(() -> new SDDException(false, "Frequency Type not found for ID: " + request.getFrequencyTypeId()));

            frequencyType.setActive(request.getActive() != null ? request.getActive() : true);
            frequencyType.setUpdatedBy(userId);

            masFrequencyTypeRepo.save(frequencyType);

            logger.info("Frequency type status updated successfully for ID: {}", request.getFrequencyTypeId());

            DefaultResponse defaultResponse = new DefaultResponse();
            defaultResponse.setMsg("Frequency type status updated successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating frequency type status", e);
            throw new SDDException(false, "Unexpected error during frequency type status update");
        }
    }

    @Override
    public ApiResponse<MasFrequencyType> getFrequencyByMenuId(GetFrequencyReq request) {
        logger.info("Fetching frequency type for ID: {}", request.getFrequencyTypeId());

        try {
            MasFrequencyType frequencyType = masFrequencyTypeRepo.findById(request.getFrequencyTypeId())
                    .orElseThrow(() -> new SDDException(false, "Frequency Type not found for ID: " + request.getFrequencyTypeId()));

            return ResponseUtils.createSuccessResponse(frequencyType, new TypeReference<MasFrequencyType>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching frequency type", e);
            throw new SDDException(false, "Unexpected error during frequency type retrieval");
        }
    }

    @Override
    public ApiResponseWithPageable<List<MasFrequencyType>> getAllFrequency(GetFrequencyReq request) {
        logger.info("Fetching all frequency types with pagination");

        try {
            Pageable pageable = helperUtill.getPageable(request.getPage(), request.getLimit());
            Page<MasFrequencyType> frequencyPage = masFrequencyTypeRepo.findAll(pageable);

            logger.info("Frequency Type details fetched successfully");

            return ResponseUtils.createPageableSuccessResponse(
                    frequencyPage.getContent(),
                    frequencyPage.getTotalElements(),
                    frequencyPage.getTotalPages(),
                    pageable.getPageNumber() + 1,
                    pageable.getPageSize()
            );

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching all frequency types", e);
            throw new SDDException(false, "Unexpected error during fetching frequency types details");
        }
    }


    //**********************MAS_SEASONALITY_FACTOR******************************/



    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<DefaultResponse> createSeasonality(Long userId, CreateSeasonalityReq request) {
        logger.info("Starting seasonality factor creation by user: {}", userId);

        try {
            MasSeasonalityFactor seasonalityFactor = MasSeasonalityFactor.builder()
                    .description(request.getDescription())
                    .isActive(true)
                    .createdBy(userId)
                    .build();

            masSeasonalityFactorRepo.save(seasonalityFactor);

            logger.info("Seasonality factor created successfully with ID: {}", seasonalityFactor.getSeasonalityFactorId());

            DefaultResponse defaultResponse = new DefaultResponse();
            defaultResponse.setMsg("Seasonality factor created successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {});

        } catch (Exception e) {
            logger.error("Error while creating seasonality factor", e);
            throw new SDDException(false, "Unexpected error during seasonality factor creation");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<DefaultResponse> updateSeasonality(Long userId, CreateSeasonalityReq request) {
        logger.info("Starting seasonality factor update for ID: {} by user: {}", request.getSeasonalityFactorId(), userId);

        try {
            MasSeasonalityFactor seasonalityFactor = masSeasonalityFactorRepo.findById(request.getSeasonalityFactorId())
                    .orElseThrow(() -> new SDDException(false, "Seasonality Factor not found for ID: " + request.getSeasonalityFactorId()));

            seasonalityFactor.setDescription(request.getDescription());
            seasonalityFactor.setUpdatedBy(userId);

            masSeasonalityFactorRepo.save(seasonalityFactor);

            logger.info("Seasonality factor updated successfully for ID: {}", request.getSeasonalityFactorId());

            DefaultResponse defaultResponse = new DefaultResponse();
            defaultResponse.setMsg("Seasonality factor updated successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating seasonality factor", e);
            throw new SDDException(false, "Unexpected error during seasonality factor update");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<DefaultResponse> updateSeasonalityStatus(Long userId, SeasonalityStatusReq request) {
        logger.info("Starting seasonality factor status update for ID: {} by user: {}", request.getSeasonalityFactorId(), userId);

        try {
            MasSeasonalityFactor seasonalityFactor = masSeasonalityFactorRepo.findById(request.getSeasonalityFactorId())
                    .orElseThrow(() -> new SDDException(false, "Seasonality Factor not found for ID: " + request.getSeasonalityFactorId()));

            seasonalityFactor.setActive(request.getActive() != null ? request.getActive() : true);
            seasonalityFactor.setUpdatedBy(userId);

            masSeasonalityFactorRepo.save(seasonalityFactor);

            logger.info("Seasonality factor status updated successfully for ID: {}", request.getSeasonalityFactorId());

            DefaultResponse defaultResponse = new DefaultResponse();
            defaultResponse.setMsg("Seasonality factor status updated successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating seasonality factor status", e);
            throw new SDDException(false, "Unexpected error during seasonality factor status update");
        }
    }

    @Override
    public ApiResponse<MasSeasonalityFactor> getSeasonalityByMenuId(GetSeasonalityReq request) {
        logger.info("Fetching seasonality factor for ID: {}", request.getSeasonalityFactorId());

        try {
            MasSeasonalityFactor seasonalityFactor = masSeasonalityFactorRepo.findById(request.getSeasonalityFactorId())
                    .orElseThrow(() -> new SDDException(false, "Seasonality Factor not found for ID: " + request.getSeasonalityFactorId()));

            return ResponseUtils.createSuccessResponse(seasonalityFactor, new TypeReference<MasSeasonalityFactor>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching seasonality factor", e);
            throw new SDDException(false, "Unexpected error during seasonality factor retrieval");
        }
    }

    @Override
    public ApiResponseWithPageable<List<MasSeasonalityFactor>> getAllSeasonality(GetSeasonalityReq request) {
        logger.info("Fetching all seasonality factors with pagination");

        try {
            Pageable pageable = helperUtill.getPageable(request.getPage(), request.getLimit());
            Page<MasSeasonalityFactor> seasonalityPage = masSeasonalityFactorRepo.findAll(pageable);

            logger.info("Seasonality Factor details fetched successfully");

            return ResponseUtils.createPageableSuccessResponse(
                    seasonalityPage.getContent(),
                    seasonalityPage.getTotalElements(),
                    seasonalityPage.getTotalPages(),
                    pageable.getPageNumber() + 1,
                    pageable.getPageSize()
            );

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching all seasonality factors", e);
            throw new SDDException(false, "Unexpected error during fetching seasonality factor details");
        }
    }


    //**********************MAS_SCHEDULE_ATTRIBUTES******************************/




    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<DefaultResponse> createScheduleAttributes(Long userId, CreateScheduleAttributesReq request) {
        logger.info("Starting schedule attributes creation by user: {}", userId);

        try {
            MasScheduleAttributes attributes = MasScheduleAttributes.builder()
                    .description(request.getDescription())
                    .type(request.getType()) // Uncomment if type is added to CreateScheduleAttributesReq
                    .isActive(true)
                    .createdBy(userId)
                    .build();

            masScheduleAttributesRepo.save(attributes);

            logger.info("Schedule attributes created successfully with ID: {}", attributes.getScheduleAttributesId());

            DefaultResponse defaultResponse = new DefaultResponse();
            defaultResponse.setMsg("Schedule attributes created successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {});

        } catch (Exception e) {
            logger.error("Error while creating schedule attributes", e);
            throw new SDDException(false, "Unexpected error during schedule attributes creation");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<DefaultResponse> updateScheduleAttributes(Long userId, CreateScheduleAttributesReq request) {
        logger.info("Starting schedule attributes update for ID: {} by user: {}", request.getScheduleAttributesId(), userId);

        try {
            MasScheduleAttributes attributes = masScheduleAttributesRepo.findById(request.getScheduleAttributesId())
                    .orElseThrow(() -> new SDDException(false, "Schedule Attributes not found for ID: " + request.getScheduleAttributesId()));

            attributes.setDescription(request.getDescription());
            attributes.setType(request.getType()); // Uncomment if type is in request
            attributes.setUpdatedBy(userId);

            masScheduleAttributesRepo.save(attributes);

            logger.info("Schedule attributes updated successfully for ID: {}", request.getScheduleAttributesId());

            DefaultResponse defaultResponse = new DefaultResponse();
            defaultResponse.setMsg("Schedule attributes updated successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating schedule attributes", e);
            throw new SDDException(false, "Unexpected error during schedule attributes update");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<DefaultResponse> updateScheduleAttributesStatus(Long userId, ScheduleAttributesStatusReq request) {
        logger.info("Starting schedule attributes status update for ID: {} by user: {}", request.getScheduleAttributesId(), userId);

        try {
            MasScheduleAttributes attributes = masScheduleAttributesRepo.findById(request.getScheduleAttributesId())
                    .orElseThrow(() -> new SDDException(false, "Schedule Attributes not found for ID: " + request.getScheduleAttributesId()));

            attributes.setActive(request.getActive() != null ? request.getActive() : true);
            attributes.setUpdatedBy(userId);

            masScheduleAttributesRepo.save(attributes);

            logger.info("Schedule attributes status updated successfully for ID: {}", request.getScheduleAttributesId());

            DefaultResponse defaultResponse = new DefaultResponse();
            defaultResponse.setMsg("Schedule attributes status updated successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating schedule attributes status", e);
            throw new SDDException(false, "Unexpected error during schedule attributes status update");
        }
    }

    @Override
    public ApiResponse<MasScheduleAttributes> getScheduleAttributesByMenuId(GetScheduleAttributesReq request) {
        logger.info("Fetching schedule attributes for ID: {}", request.getScheduleAttributesId());

        try {
            MasScheduleAttributes attributes = masScheduleAttributesRepo.findById(request.getScheduleAttributesId())
                    .orElseThrow(() -> new SDDException(false, "Schedule Attributes not found for ID: " + request.getScheduleAttributesId()));

            return ResponseUtils.createSuccessResponse(attributes, new TypeReference<MasScheduleAttributes>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching schedule attributes", e);
            throw new SDDException(false, "Unexpected error during schedule attributes retrieval");
        }
    }

    @Override
    public ApiResponseWithPageable<List<MasScheduleAttributes>> getAllScheduleAttributes(GetScheduleAttributesReq request) {
        logger.info("Fetching all schedule attributes with pagination");

        try {
            Pageable pageable = helperUtill.getPageable(request.getPage(), request.getLimit());
            Page<MasScheduleAttributes> attributesPage = masScheduleAttributesRepo.findAll(pageable);

            logger.info("Schedule attributes details fetched successfully");

            return ResponseUtils.createPageableSuccessResponse(
                    attributesPage.getContent(),
                    attributesPage.getTotalElements(),
                    attributesPage.getTotalPages(),
                    pageable.getPageNumber() + 1,
                    pageable.getPageSize()
            );

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching all schedule attributes", e);
            throw new SDDException(false, "Unexpected error during fetching schedule attributes details");
        }
    }



    //**********************MAS_STATUS******************************/



    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<DefaultResponse> createStatus(Long userId, CreateStatusReq request) {
        logger.info("Starting status creation by user: {}", userId);

        try {
            MasStatus status = MasStatus.builder()
                    .description(request.getDescription())
                    .isActive(true)
                    .createdBy(userId)
                    .build();

            masStatusRepo.save(status);

            logger.info("Status created successfully with ID: {}", status.getStatusId());

            DefaultResponse defaultResponse = new DefaultResponse();
            defaultResponse.setMsg("Status created successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {});

        } catch (Exception e) {
            logger.error("Error while creating status", e);
            throw new SDDException(false, "Unexpected error during status creation");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<DefaultResponse> updateStatus(Long userId, CreateStatusReq request) {
        logger.info("Starting status update for ID: {} by user: {}", request.getStatusId(), userId);

        try {
            MasStatus status = masStatusRepo.findById(request.getStatusId())
                    .orElseThrow(() -> new SDDException(false, "Status not found for ID: " + request.getStatusId()));

            status.setDescription(request.getDescription());
            status.setUpdatedBy(userId);

            masStatusRepo.save(status);

            logger.info("Status updated successfully for ID: {}", request.getStatusId());

            DefaultResponse defaultResponse = new DefaultResponse();
            defaultResponse.setMsg("Status updated successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating status", e);
            throw new SDDException(false, "Unexpected error during status update");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<DefaultResponse> updateStatusStatus(Long userId, StatusStatusReq request) {
        logger.info("Starting status status update for ID: {} by user: {}", request.getStatusId(), userId);

        try {
            MasStatus status = masStatusRepo.findById(request.getStatusId())
                    .orElseThrow(() -> new SDDException(false, "Status not found for ID: " + request.getStatusId()));

            status.setActive(request.getActive() != null ? request.getActive() : true);
            status.setUpdatedBy(userId);

            masStatusRepo.save(status);

            logger.info("Status status updated successfully for ID: {}", request.getStatusId());

            DefaultResponse defaultResponse = new DefaultResponse();
            defaultResponse.setMsg("Status status updated successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating status status", e);
            throw new SDDException(false, "Unexpected error during status status update");
        }
    }

    @Override
    public ApiResponse<MasStatus> getStatusByMenuId(GetStatusReq request) {
        logger.info("Fetching status for ID: {}", request.getStatusId());

        try {
            MasStatus status = masStatusRepo.findById(request.getStatusId())
                    .orElseThrow(() -> new SDDException(false, "Status not found for ID: " + request.getStatusId()));

            return ResponseUtils.createSuccessResponse(status, new TypeReference<MasStatus>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching status", e);
            throw new SDDException(false, "Unexpected error during status retrieval");
        }
    }

    @Override
    public ApiResponseWithPageable<List<MasStatus>> getAllStatus(GetStatusReq request) {
        logger.info("Fetching all statuses with pagination");

        try {
            Pageable pageable = helperUtill.getPageable(request.getPage(), request.getLimit());
            Page<MasStatus> statusPage = masStatusRepo.findAll(pageable);

            logger.info("Status details fetched successfully");

            return ResponseUtils.createPageableSuccessResponse(
                    statusPage.getContent(),
                    statusPage.getTotalElements(),
                    statusPage.getTotalPages(),
                    pageable.getPageNumber() + 1,
                    pageable.getPageSize()
            );

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching all statuses", e);
            throw new SDDException(false, "Unexpected error during fetching status details");
        }
    }


    //**********************MAS_COMPARISON******************************/


    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<DefaultResponse> createComparison(Long userId, CreateComparisonReq request) {
        logger.info("Starting comparison creation with description: {} by user: {}", request.getDescription(), userId);

        try {
            // Optional: Check if description already exists to prevent duplicate unique constraint errors
            if (request.getDescription() != null && masComparisonRepo.findByDescription(request.getDescription()).isPresent()) {
                throw new SDDException(false, "Comparison with description already exists: " + request.getDescription());
            }

            MasComparison comparison = MasComparison.builder()
                    .description(request.getDescription())
                    .symbol(request.getSymbol())
                    .isActive(true)
                    .createdBy(userId)
                    .build();

            masComparisonRepo.save(comparison);
            logger.info("Comparison created successfully with ID: {}", comparison.getComparisonId());

            DefaultResponse defaultResponse = new DefaultResponse();
            defaultResponse.setMsg("Comparison created successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while creating comparison", e);
            throw new SDDException(false, "Unexpected error during comparison creation");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<DefaultResponse> updateComparison(Long userId, CreateComparisonReq request) {
        logger.info("Starting comparison update for ID: {} by user: {}", request.getComparisonId(), userId);

        try {
            if (request.getComparisonId() == null) {
                throw new SDDException(false, "Comparison ID must be provided for update");
            }

            MasComparison comparison = masComparisonRepo.findById(request.getComparisonId())
                    .orElseThrow(() -> new SDDException(false, "Comparison not found for ID: " + request.getComparisonId()));

            comparison.setDescription(request.getDescription());
            comparison.setSymbol(request.getSymbol());
            comparison.setUpdatedBy(userId);

            masComparisonRepo.save(comparison);
            logger.info("Comparison updated successfully for ID: {}", request.getComparisonId());

            DefaultResponse defaultResponse = new DefaultResponse();
            defaultResponse.setMsg("Comparison updated successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating comparison", e);
            throw new SDDException(false, "Unexpected error during comparison update");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<DefaultResponse> updateComparisonStatus(Long userId, ComparisonStatusReq request) {
        logger.info("Starting comparison status update for ID: {} by user: {}", request.getComparisonId(), userId);

        try {
            MasComparison comparison = masComparisonRepo.findById(request.getComparisonId())
                    .orElseThrow(() -> new SDDException(false, "Comparison not found for ID: " + request.getComparisonId()));

            comparison.setActive(request.getActive() != null ? request.getActive() : true);
            comparison.setUpdatedBy(userId);

            masComparisonRepo.save(comparison);
            logger.info("Comparison status updated successfully for ID: {}", request.getComparisonId());

            DefaultResponse defaultResponse = new DefaultResponse();
            defaultResponse.setMsg("Comparison status updated successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating comparison status", e);
            throw new SDDException(false, "Unexpected error during comparison status update");
        }
    }

    @Override
    public ApiResponseWithPageable<List<MasComparison>> getComparisonByIdOrUser(GetComparisonReq request) {
        logger.info("Fetching comparison details for comparisonId: {} with pagination", request.getComparisonId());

        try {
            Pageable pageable = helperUtill.getPageable(request.getPage(), request.getLimit());
            Page<MasComparison> comparisonPage;

            if (request.getComparisonId() != null) {
                Optional<MasComparison> optionalComparison = masComparisonRepo.findById(request.getComparisonId());
                List<MasComparison> content = optionalComparison.map(List::of).orElse(List.of());
                long totalElements = optionalComparison.isPresent() ? 1 : 0;

                comparisonPage = new PageImpl<>(content, pageable, totalElements);
            } else {
                throw new SDDException(false, "Comparison ID must be provided");
            }

            if (comparisonPage.getContent().isEmpty()) {
                throw new SDDException(false, "No comparison details found");
            }

            logger.info("Comparison details fetched successfully");

            return ResponseUtils.createPageableSuccessResponse(
                    comparisonPage.getContent(),
                    comparisonPage.getTotalElements(),
                    comparisonPage.getTotalPages(),
                    pageable.getPageNumber() + 1,
                    pageable.getPageSize()
            );

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching comparison details", e);
            throw new SDDException(false, "Unexpected error during comparison retrieval");
        }
    }

    @Override
    public ApiResponseWithPageable<List<MasComparison>> getAllComparison(GetComparisonReq request) {
        logger.info("Fetching all comparisons with pagination");

        try {
            Pageable pageable = helperUtill.getPageable(request.getPage(), request.getLimit());
            Page<MasComparison> comparisonPage = masComparisonRepo.findAll(pageable);

            if (comparisonPage.getContent().isEmpty()) {
                throw new SDDException(false, "No comparisons found");
            }

            logger.info("All comparison list fetched successfully");

            return ResponseUtils.createPageableSuccessResponse(
                    comparisonPage.getContent(),
                    comparisonPage.getTotalElements(),
                    comparisonPage.getTotalPages(),
                    pageable.getPageNumber() + 1,
                    pageable.getPageSize()
            );

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching all comparisons", e);
            throw new SDDException(false, "Unexpected error during all comparisons retrieval");
        }
    }



    //**********************MAS_VALIDATION******************************/



    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<DefaultResponse> createValidation(Long userId, CreateValidationReq request) {
        logger.info("Starting validation creation with description: {} by user: {}", request.getDescription(), userId);

        try {
            if (request.getDescription() != null && masValidationRepo.findByDescription(request.getDescription()).isPresent()) {
                throw new SDDException(false, "Validation with description already exists: " + request.getDescription());
            }

            MasValidation validation = MasValidation.builder()
                    .description(request.getDescription())
                    .isActive(true)
                    .createdBy(userId)
                    .build();

            masValidationRepo.save(validation);
            logger.info("Validation created successfully with ID: {}", validation.getValidationId());

            DefaultResponse defaultResponse = new DefaultResponse();
            defaultResponse.setMsg("Validation created successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while creating validation", e);
            throw new SDDException(false, "Unexpected error during validation creation");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<DefaultResponse> updateValidation(Long userId, CreateValidationReq request) {
        logger.info("Starting validation update for ID: {} by user: {}", request.getValidationId(), userId);

        try {
            if (request.getValidationId() == null) {
                throw new SDDException(false, "Validation ID must be provided for update");
            }

            MasValidation validation = masValidationRepo.findById(request.getValidationId())
                    .orElseThrow(() -> new SDDException(false, "Validation not found for ID: " + request.getValidationId()));

            validation.setDescription(request.getDescription());
            validation.setUpdatedBy(userId);

            masValidationRepo.save(validation);
            logger.info("Validation updated successfully for ID: {}", request.getValidationId());

            DefaultResponse defaultResponse = new DefaultResponse();
            defaultResponse.setMsg("Validation updated successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating validation", e);
            throw new SDDException(false, "Unexpected error during validation update");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<DefaultResponse> updateValidationStatus(Long userId, ValidationStatusReq request) {
        logger.info("Starting validation status update for ID: {} by user: {}", request.getValidationId(), userId);

        try {
            MasValidation validation = masValidationRepo.findById(request.getValidationId())
                    .orElseThrow(() -> new SDDException(false, "Validation not found for ID: " + request.getValidationId()));

            validation.setActive(request.getActive() != null ? request.getActive() : true);
            validation.setUpdatedBy(userId);

            masValidationRepo.save(validation);
            logger.info("Validation status updated successfully for ID: {}", request.getValidationId());

            DefaultResponse defaultResponse = new DefaultResponse();
            defaultResponse.setMsg("Validation status updated successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating validation status", e);
            throw new SDDException(false, "Unexpected error during validation status update");
        }
    }

    @Override
    public ApiResponseWithPageable<List<MasValidation>> getValidationByIdOrUser(GetValidationReq request) {
        logger.info("Fetching validation details for validationId: {} with pagination", request.getValidationId());

        try {
            Pageable pageable = helperUtill.getPageable(request.getPage(), request.getLimit());
            Page<MasValidation> validationPage;

            if (request.getValidationId() != null) {
                Optional<MasValidation> optionalValidation = masValidationRepo.findById(request.getValidationId());
                List<MasValidation> content = optionalValidation.map(List::of).orElse(List.of());
                long totalElements = optionalValidation.isPresent() ? 1 : 0;

                validationPage = new PageImpl<>(content, pageable, totalElements);
            } else {
                throw new SDDException(false, "Validation ID must be provided");
            }

            if (validationPage.getContent().isEmpty()) {
                throw new SDDException(false, "No validation details found");
            }

            logger.info("Validation details fetched successfully");

            return ResponseUtils.createPageableSuccessResponse(
                    validationPage.getContent(),
                    validationPage.getTotalElements(),
                    validationPage.getTotalPages(),
                    pageable.getPageNumber() + 1,
                    pageable.getPageSize()
            );

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching validation details", e);
            throw new SDDException(false, "Unexpected error during validation retrieval");
        }
    }

    @Override
    public ApiResponseWithPageable<List<MasValidation>> getAllValidation(GetValidationReq request) {
        logger.info("Fetching all validations with pagination");

        try {
            Pageable pageable = helperUtill.getPageable(request.getPage(), request.getLimit());
            Page<MasValidation> validationPage = masValidationRepo.findAll(pageable);

            if (validationPage.getContent().isEmpty()) {
                throw new SDDException(false, "No validations found");
            }

            logger.info("All validation list fetched successfully");

            return ResponseUtils.createPageableSuccessResponse(
                    validationPage.getContent(),
                    validationPage.getTotalElements(),
                    validationPage.getTotalPages(),
                    pageable.getPageNumber() + 1,
                    pageable.getPageSize()
            );

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching all validations", e);
            throw new SDDException(false, "Unexpected error during all validations retrieval");
        }
    }


}
