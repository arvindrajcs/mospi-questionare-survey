package com.mospi.questionare_survey.controller;


import com.mospi.questionare_survey.entity.*;
import com.mospi.questionare_survey.request.*;
import com.mospi.questionare_survey.response.ApiResponse;
import com.mospi.questionare_survey.response.ApiResponseWithPageable;
import com.mospi.questionare_survey.response.DefaultResponse;
import com.mospi.questionare_survey.service.MasterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mas")
@CrossOrigin(origins = "*")
@Tag(name = "masterController",description = "This controller only for Master Table Data.")
@Slf4j
public class MasterController {

    private final Logger logger = LoggerFactory.getLogger(MasterController.class);

    @Autowired
    private MasterService masterService;


    //**********************MAS_MENU******************************/

    @Operation(summary = "This API is used to create Menu with basic Details")
    @PostMapping("/menu/create-menu")
    public ResponseEntity<ApiResponse<DefaultResponse>> createMenu(@RequestHeader("userId") Long userId, @RequestBody CreateMenuReq request) {
        logger.info("trying to call createMenu API");
        return new ResponseEntity<>(masterService.createMenu(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to update Menu with basic Details")
    @PostMapping("/menu/update-menu")
    public ResponseEntity<ApiResponse<DefaultResponse>> updateMenu(@RequestHeader("userId") Long userId, @RequestBody CreateMenuReq request) {
        logger.info("trying to call updateMenu API");
        return new ResponseEntity<>(masterService.updateMenu(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to update Menu status Active or InActive")
    @PostMapping("/menu/update-menu-status")
    public ResponseEntity<ApiResponse<DefaultResponse>> updateMenuStatus(@RequestHeader("userId") Long userId, @RequestBody MenuStatusReq request) {
        logger.info("trying to call updateMenuStatus API");
        return new ResponseEntity<>(masterService.updateMenuStatus(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to Get Menu using MenuId")
    @PostMapping("/menu/get-menu")
    public ResponseEntity<ApiResponse<MasMenu>> getMenuByMenuId(@RequestBody GetMenuReq request) {
        logger.info("trying to call getMenuByMenuId API");
        return new ResponseEntity<>(masterService.getMenuByMenuId(request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to Get All Menu List")
    @PostMapping("/menu/all-menu")
    public ResponseEntity<ApiResponseWithPageable<List<MasMenu>>> getAllMenus(@RequestBody GetMenuReq request) {
        logger.info("Trying to call getAllMenus");
        return new ResponseEntity<>(masterService.getAllMenus(request), HttpStatus.OK);
    }


    //**********************MAS_ROLE******************************/

    @Operation(summary = "This API is used to create Role with basic Details")
    @PostMapping("/role/create-role")
    public ResponseEntity<ApiResponse<DefaultResponse>> createRole(@RequestHeader("userId") Long userId, @RequestBody CreateRoleReq request) {
        logger.info("trying to call createRole API");
        return new ResponseEntity<>(masterService.createRole(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to update Role with basic Details")
    @PostMapping("/role/update-role")
    public ResponseEntity<ApiResponse<DefaultResponse>> updateRole(@RequestHeader("userId") Long userId, @RequestBody CreateRoleReq request) {
        logger.info("trying to call updateRole API");
        return new ResponseEntity<>(masterService.updateRole(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to update Role status Active or InActive")
    @PostMapping("/role/update-role-status")
    public ResponseEntity<ApiResponse<DefaultResponse>> updateRoleStatus(@RequestHeader("userId") Long userId, @RequestBody RoleStatusReq request) {
        logger.info("trying to call updateRoleStatus API");
        return new ResponseEntity<>(masterService.updateRoleStatus(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to Get Role using RoleId")
    @PostMapping("/role/get-role")
    public ResponseEntity<ApiResponse<MasRole>> getRoleByRoleId(@RequestBody GetRoleReq request) {
        logger.info("trying to call getRoleByRoleId API");
        return new ResponseEntity<>(masterService.getRoleByRoleId(request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to Get All Role List")
    @PostMapping("/role/all-role")
    public ResponseEntity<ApiResponseWithPageable<List<MasRole>>> getAllRole(@RequestBody GetRoleReq request) {
        logger.info("Trying to call getAllRole");
        return new ResponseEntity<>(masterService.getAllRole(request), HttpStatus.OK);
    }


    //**********************MAS_SCHEDULE_TYPE******************************/


    @Operation(summary = "This API is used to create ScheduleType with basic Details")
    @PostMapping("/schedule/create-schedule-type")
    public ResponseEntity<ApiResponse<DefaultResponse>> createScheduleType(@RequestHeader("userId") Long userId, @RequestBody CreateScheduleTypeReq request) {
        logger.info("trying to call createScheduleType API");
        return new ResponseEntity<>(masterService.createScheduleType(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to update ScheduleType with basic Details")
    @PostMapping("/schedule/update-schedule-type")
    public ResponseEntity<ApiResponse<DefaultResponse>> updateScheduleType(@RequestHeader("userId") Long userId, @RequestBody CreateScheduleTypeReq request) {
        logger.info("trying to call updateScheduleType API");
        return new ResponseEntity<>(masterService.updateScheduleType(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to update ScheduleType status Active or InActive")
    @PostMapping("/schedule/update-schedule-type-status")
    public ResponseEntity<ApiResponse<DefaultResponse>> updateScheduleTypeStatus(@RequestHeader("userId") Long userId, @RequestBody ScheduleTypeStatusReq request) {
        logger.info("trying to call updateScheduleTypeStatus API");
        return new ResponseEntity<>(masterService.updateScheduleTypeStatus(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to Get ScheduleType using ScheduleTypeId")
    @PostMapping("/schedule/get-schedule-type")
    public ResponseEntity<ApiResponse<MasScheduleType>> getScheduleTypeByMenuId(@RequestBody GetScheduleTypeReq request) {
        logger.info("trying to call getScheduleTypeByMenuId API");
        return new ResponseEntity<>(masterService.getScheduleTypeByMenuId(request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to Get All ScheduleType List")
    @PostMapping("/schedule/all-schedule-type")
    public ResponseEntity<ApiResponseWithPageable<List<MasScheduleType>>> getAllScheduleType(@RequestBody GetScheduleTypeReq request) {
        logger.info("Trying to call getAllScheduleType");
        return new ResponseEntity<>(masterService.getAllScheduleType(request), HttpStatus.OK);
    }

    //**********************MAS_SURVEY_TYPE******************************/


    @Operation(summary = "This API is used to create SurveyType with basic Details")
    @PostMapping("/survey/create-survey-type")
    public ResponseEntity<ApiResponse<DefaultResponse>> createSurveyType(@RequestHeader("userId") Long userId, @RequestBody CreateSurveyTypeReq request) {
        logger.info("trying to call createSurveyType API");
        return new ResponseEntity<>(masterService.createSurveyType(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to update SurveyType with basic Details")
    @PostMapping("/survey/update-survey-type")
    public ResponseEntity<ApiResponse<DefaultResponse>> updateSurveyType(@RequestHeader("userId") Long userId, @RequestBody CreateSurveyTypeReq request) {
        logger.info("trying to call updateSurveyType API");
        return new ResponseEntity<>(masterService.updateSurveyType(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to update SurveyType status Active or InActive")
    @PostMapping("/survey/update-survey-type-status")
    public ResponseEntity<ApiResponse<DefaultResponse>> updateSurveyTypeStatus(@RequestHeader("userId") Long userId, @RequestBody SurveyTypeStatusReq request) {
        logger.info("trying to call updateSurveyTypeStatus API");
        return new ResponseEntity<>(masterService.updateSurveyTypeStatus(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to Get SurveyType using SurveyTypeId")
    @PostMapping("/survey/get-survey-type")
    public ResponseEntity<ApiResponse<MasSurveyType>> getSurveyTypeByMenuId(@RequestBody GetSurveyTypeReq request) {
        logger.info("trying to call getSurveyTypeByMenuId API");
        return new ResponseEntity<>(masterService.getSurveyTypeByMenuId(request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to Get All SurveyType List")
    @PostMapping("/survey/all-survey-type")
    public ResponseEntity<ApiResponseWithPageable<List<MasSurveyType>>> getAllSurveyType(@RequestBody GetSurveyTypeReq request) {
        logger.info("Trying to call getAllSurveyType");
        return new ResponseEntity<>(masterService.getAllSurveyType(request), HttpStatus.OK);
    }

    //**********************MAS_DIVISION******************************/

    @Operation(summary = "This API is used to create Division with basic Details")
    @PostMapping("/division/create-division")
    public ResponseEntity<ApiResponse<DefaultResponse>> createDivision(@RequestHeader("userId") Long userId, @RequestBody CreateDivisionReq request) {
        logger.info("trying to call createDivision API");
        return new ResponseEntity<>(masterService.createDivision(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to update Division with basic Details")
    @PostMapping("/division/update-division")
    public ResponseEntity<ApiResponse<DefaultResponse>> updateDivision(@RequestHeader("userId") Long userId, @RequestBody CreateDivisionReq request) {
        logger.info("trying to call updateDivision API");
        return new ResponseEntity<>(masterService.updateDivision(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to update Division status Active or InActive")
    @PostMapping("/division/update-division-status")
    public ResponseEntity<ApiResponse<DefaultResponse>> updateDivisionStatus(@RequestHeader("userId") Long userId, @RequestBody DivisionStatusReq request) {
        logger.info("trying to call updateDivisionStatus API");
        return new ResponseEntity<>(masterService.updateDivisionStatus(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to Get Division using DivisionId")
    @PostMapping("/division/get-division")
    public ResponseEntity<ApiResponse<MasDivision>> getDivisionByMenuId(@RequestBody GetDivisionReq request) {
        logger.info("trying to call getDivisionByMenuId API");
        return new ResponseEntity<>(masterService.getDivisionByMenuId(request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to Get All Division List")
    @PostMapping("/division/all-division")
    public ResponseEntity<ApiResponseWithPageable<List<MasDivision>>> getAllDivision(@RequestBody GetDivisionReq request) {
        logger.info("Trying to call getAllDivision");
        return new ResponseEntity<>(masterService.getAllDivision(request), HttpStatus.OK);
    }

    //**********************MAS_ENQUIRY_TYPE******************************/

    @Operation(summary = "This API is used to create Enquiry with basic Details")
    @PostMapping("/enquiry/create-enquiry")
    public ResponseEntity<ApiResponse<DefaultResponse>> createEnquiry(@RequestHeader("userId") Long userId, @RequestBody CreateEnquiryReq request) {
        logger.info("trying to call createEnquiry API");
        return new ResponseEntity<>(masterService.createEnquiry(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to update Enquiry with basic Details")
    @PostMapping("/enquiry/update-enquiry")
    public ResponseEntity<ApiResponse<DefaultResponse>> updateEnquiry(@RequestHeader("userId") Long userId, @RequestBody CreateEnquiryReq request) {
        logger.info("trying to call updateEnquiry API");
        return new ResponseEntity<>(masterService.updateEnquiry(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to update Enquiry status Active or InActive")
    @PostMapping("/enquiry/update-enquiry-status")
    public ResponseEntity<ApiResponse<DefaultResponse>> updateEnquiryStatus(@RequestHeader("userId") Long userId, @RequestBody EnquiryStatusReq request) {
        logger.info("trying to call updateEnquiryStatus API");
        return new ResponseEntity<>(masterService.updateEnquiryStatus(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to Get Enquiry using EnquiryId")
    @PostMapping("/enquiry/get-enquiry")
    public ResponseEntity<ApiResponse<MasEnquiryType>> getEnquiryByMenuId(@RequestBody GetEnquiryReq request) {
        logger.info("trying to call getEnquiryByMenuId API");
        return new ResponseEntity<>(masterService.getEnquiryByMenuId(request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to Get All Enquiry List")
    @PostMapping("/enquiry/all-enquiry")
    public ResponseEntity<ApiResponseWithPageable<List<MasEnquiryType>>> getAllEnquiry(@RequestBody GetEnquiryReq request) {
        logger.info("Trying to call getAllEnquiry");
        return new ResponseEntity<>(masterService.getAllEnquiry(request), HttpStatus.OK);
    }

    //**********************MAS_FREQUENCY_TYPE******************************/

    @Operation(summary = "This API is used to create Frequency with basic Details")
    @PostMapping("/frequency/create-frequency")
    public ResponseEntity<ApiResponse<DefaultResponse>> createFrequency(@RequestHeader("userId") Long userId, @RequestBody CreateFrequencyReq request) {
        logger.info("trying to call createFrequency API");
        return new ResponseEntity<>(masterService.createFrequency(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to update Frequency with basic Details")
    @PostMapping("/frequency/update-frequency")
    public ResponseEntity<ApiResponse<DefaultResponse>> updateFrequency(@RequestHeader("userId") Long userId, @RequestBody CreateFrequencyReq request) {
        logger.info("trying to call updateFrequency API");
        return new ResponseEntity<>(masterService.updateFrequency(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to update Frequency status Active or InActive")
    @PostMapping("/frequency/update-frequency-status")
    public ResponseEntity<ApiResponse<DefaultResponse>> updateFrequencyStatus(@RequestHeader("userId") Long userId, @RequestBody FrequencyStatusReq request) {
        logger.info("trying to call updateFrequencyStatus API");
        return new ResponseEntity<>(masterService.updateFrequencyStatus(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to Get Frequency using FrequencyId")
    @PostMapping("/frequency/get-frequency")
    public ResponseEntity<ApiResponse<MasFrequencyType>> getFrequencyByMenuId(@RequestBody GetFrequencyReq request) {
        logger.info("trying to call getFrequencyByMenuId API");
        return new ResponseEntity<>(masterService.getFrequencyByMenuId(request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to Get All Frequency List")
    @PostMapping("/frequency/all-frequency")
    public ResponseEntity<ApiResponseWithPageable<List<MasFrequencyType>>> getAllFrequency(@RequestBody GetFrequencyReq request) {
        logger.info("Trying to call getAllFrequency");
        return new ResponseEntity<>(masterService.getAllFrequency(request), HttpStatus.OK);
    }


    //**********************MAS_SEASONALITY_FACTOR******************************/

    @Operation(summary = "This API is used to create Seasonality with basic Details")
    @PostMapping("/seasonality/create-seasonality")
    public ResponseEntity<ApiResponse<DefaultResponse>> createSeasonality(@RequestHeader("userId") Long userId, @RequestBody CreateSeasonalityReq request) {
        logger.info("trying to call createSeasonality API");
        return new ResponseEntity<>(masterService.createSeasonality(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to update Seasonality with basic Details")
    @PostMapping("/seasonality/update-seasonality")
    public ResponseEntity<ApiResponse<DefaultResponse>> updateSeasonality(@RequestHeader("userId") Long userId, @RequestBody CreateSeasonalityReq request) {
        logger.info("trying to call updateSeasonality API");
        return new ResponseEntity<>(masterService.updateSeasonality(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to update Seasonality status Active or InActive")
    @PostMapping("/seasonality/update-seasonality-status")
    public ResponseEntity<ApiResponse<DefaultResponse>> updateSeasonalityStatus(@RequestHeader("userId") Long userId, @RequestBody SeasonalityStatusReq request) {
        logger.info("trying to call updateSeasonalityStatus API");
        return new ResponseEntity<>(masterService.updateSeasonalityStatus(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to Get Seasonality using SeasonalityId")
    @PostMapping("/seasonality/get-seasonality")
    public ResponseEntity<ApiResponse<MasSeasonalityFactor>> getSeasonalityByMenuId(@RequestBody GetSeasonalityReq request) {
        logger.info("trying to call getSeasonalityByMenuId API");
        return new ResponseEntity<>(masterService.getSeasonalityByMenuId(request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to Get All Seasonality List")
    @PostMapping("/seasonality/all-seasonality")
    public ResponseEntity<ApiResponseWithPageable<List<MasSeasonalityFactor>>> getAllSeasonality(@RequestBody GetSeasonalityReq request) {
        logger.info("Trying to call getAllSeasonality");
        return new ResponseEntity<>(masterService.getAllSeasonality(request), HttpStatus.OK);
    }


    //**********************MAS_SCHEDULE_ATTRIBUTES******************************/

    @Operation(summary = "This API is used to create ScheduleAttributes with basic Details")
    @PostMapping("/schedule_attributes/create-schedule_attributes")
    public ResponseEntity<ApiResponse<DefaultResponse>> createScheduleAttributes(@RequestHeader("userId") Long userId, @RequestBody CreateScheduleAttributesReq request) {
        logger.info("trying to call createScheduleAttributes API");
        return new ResponseEntity<>(masterService.createScheduleAttributes(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to update ScheduleAttributes with basic Details")
    @PostMapping("/schedule_attributes/update-schedule_attributes")
    public ResponseEntity<ApiResponse<DefaultResponse>> updateScheduleAttributes(@RequestHeader("userId") Long userId, @RequestBody CreateScheduleAttributesReq request) {
        logger.info("trying to call updateScheduleAttributes API");
        return new ResponseEntity<>(masterService.updateScheduleAttributes(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to update ScheduleAttributes status Active or InActive")
    @PostMapping("/schedule_attributes/update-schedule_attributes-status")
    public ResponseEntity<ApiResponse<DefaultResponse>> updateScheduleAttributesStatus(@RequestHeader("userId") Long userId, @RequestBody ScheduleAttributesStatusReq request) {
        logger.info("trying to call updateScheduleAttributesStatus API");
        return new ResponseEntity<>(masterService.updateScheduleAttributesStatus(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to Get ScheduleAttributes using ScheduleAttributesId")
    @PostMapping("/schedule_attributes/get-schedule_attributes")
    public ResponseEntity<ApiResponse<MasScheduleAttributes>> getScheduleAttributesByMenuId(@RequestBody GetScheduleAttributesReq request) {
        logger.info("trying to call getScheduleAttributesByMenuId API");
        return new ResponseEntity<>(masterService.getScheduleAttributesByMenuId(request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to Get All ScheduleAttributes List")
    @PostMapping("/schedule_attributes/all-schedule_attributes")
    public ResponseEntity<ApiResponseWithPageable<List<MasScheduleAttributes>>> getAllScheduleAttributes(@RequestBody GetScheduleAttributesReq request) {
        logger.info("Trying to call getAllScheduleAttributes");
        return new ResponseEntity<>(masterService.getAllScheduleAttributes(request), HttpStatus.OK);
    }


    //**********************MAS_STATUS******************************/

    @Operation(summary = "This API is used to create Status with basic Details")
    @PostMapping("/status/create-status")
    public ResponseEntity<ApiResponse<DefaultResponse>> createStatus(@RequestHeader("userId") Long userId, @RequestBody CreateStatusReq request) {
        logger.info("trying to call createStatus API");
        return new ResponseEntity<>(masterService.createStatus(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to update Status with basic Details")
    @PostMapping("/status/update-status")
    public ResponseEntity<ApiResponse<DefaultResponse>> updateStatus(@RequestHeader("userId") Long userId, @RequestBody CreateStatusReq request) {
        logger.info("trying to call updateStatus API");
        return new ResponseEntity<>(masterService.updateStatus(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to update Status status Active or InActive")
    @PostMapping("/status/update-status-status")
    public ResponseEntity<ApiResponse<DefaultResponse>> updateStatusStatus(@RequestHeader("userId") Long userId, @RequestBody StatusStatusReq request) {
        logger.info("trying to call updateStatusStatus API");
        return new ResponseEntity<>(masterService.updateStatusStatus(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to Get Status using StatusId")
    @PostMapping("/status/get-status")
    public ResponseEntity<ApiResponse<MasStatus>> getStatusByMenuId(@RequestBody GetStatusReq request) {
        logger.info("trying to call getStatusByMenuId API");
        return new ResponseEntity<>(masterService.getStatusByMenuId(request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to Get All Status List")
    @PostMapping("/status/all-status")
    public ResponseEntity<ApiResponseWithPageable<List<MasStatus>>> getAllStatus(@RequestBody GetStatusReq request) {
        logger.info("Trying to call getAllStatus");
        return new ResponseEntity<>(masterService.getAllStatus(request), HttpStatus.OK);
    }

    //**********************MAS_COMPARISON******************************/

    @Operation(summary = "This API is used to create Comparison with basic Details")
    @PostMapping("/comparison/create-comparison")
    public ResponseEntity<ApiResponse<DefaultResponse>> createComparison(@RequestHeader("userId") Long userId, @RequestBody CreateComparisonReq request) {
        logger.info("Trying to call createComparison API");
        return new ResponseEntity<>(masterService.createComparison(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to update Comparison with basic Details")
    @PostMapping("/comparison/update-comparison")
    public ResponseEntity<ApiResponse<DefaultResponse>> updateComparison(@RequestHeader("userId") Long userId, @RequestBody CreateComparisonReq request) {
        logger.info("Trying to call updateComparison API for ID: {}", request.getComparisonId());
        return new ResponseEntity<>(masterService.updateComparison(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to update Comparison status Active or InActive")
    @PostMapping("/comparison/update-comparison-status")
    public ResponseEntity<ApiResponse<DefaultResponse>> updateComparisonStatus(@RequestHeader("userId") Long userId, @RequestBody ComparisonStatusReq request) {
        logger.info("Trying to call updateComparisonStatus API for ID: {}", request.getComparisonId());
        return new ResponseEntity<>(masterService.updateComparisonStatus(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to Get Comparison using ComparisonId")
    @PostMapping("/comparison/get-comparison")
    public ResponseEntity<ApiResponseWithPageable<List<MasComparison>>> getComparisonById(@RequestBody GetComparisonReq request) {
        logger.info("Trying to call getComparisonById API");
        return new ResponseEntity<>(masterService.getComparisonByIdOrUser(request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to Get All Comparison List with pagination")
    @PostMapping("/comparison/all-comparison")
    public ResponseEntity<ApiResponseWithPageable<List<MasComparison>>> getAllComparison(@RequestBody GetComparisonReq request) {
        logger.info("Trying to call getAllComparison API");
        return new ResponseEntity<>(masterService.getAllComparison(request), HttpStatus.OK);
    }

//**********************MAS_VALIDATION******************************/

    @Operation(summary = "This API is used to create Validation with basic Details")
    @PostMapping("/validation/create-validation")
    public ResponseEntity<ApiResponse<DefaultResponse>> createValidation(@RequestHeader("userId") Long userId, @RequestBody CreateValidationReq request) {
        logger.info("Trying to call createValidation API");
        return new ResponseEntity<>(masterService.createValidation(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to update Validation with basic Details")
    @PostMapping("/validation/update-validation")
    public ResponseEntity<ApiResponse<DefaultResponse>> updateValidation(@RequestHeader("userId") Long userId, @RequestBody CreateValidationReq request) {
        logger.info("Trying to call updateValidation API for ID: {}", request.getValidationId());
        return new ResponseEntity<>(masterService.updateValidation(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to update Validation status Active or InActive")
    @PostMapping("/validation/update-validation-status")
    public ResponseEntity<ApiResponse<DefaultResponse>> updateValidationStatus(@RequestHeader("userId") Long userId, @RequestBody ValidationStatusReq request) {
        logger.info("Trying to call updateValidationStatus API for ID: {}", request.getValidationId());
        return new ResponseEntity<>(masterService.updateValidationStatus(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to Get Validation using ValidationId")
    @PostMapping("/validation/get-validation")
    public ResponseEntity<ApiResponseWithPageable<List<MasValidation>>> getValidationById(@RequestBody GetValidationReq request) {
        logger.info("Trying to call getValidationById API");
        return new ResponseEntity<>(masterService.getValidationByIdOrUser(request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to Get All Validation List with pagination")
    @PostMapping("/validation/all-validation")
    public ResponseEntity<ApiResponseWithPageable<List<MasValidation>>> getAllValidation(@RequestBody GetValidationReq request) {
        logger.info("Trying to call getAllValidation API");
        return new ResponseEntity<>(masterService.getAllValidation(request), HttpStatus.OK);
    }


}
