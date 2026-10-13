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
}
