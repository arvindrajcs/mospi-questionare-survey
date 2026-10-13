package com.mospi.questionare_survey.service;

import com.mospi.questionare_survey.entity.*;
import com.mospi.questionare_survey.request.*;
import com.mospi.questionare_survey.response.ApiResponse;
import com.mospi.questionare_survey.response.ApiResponseWithPageable;
import com.mospi.questionare_survey.response.DefaultResponse;

import java.util.List;

public interface MasterService {


    //**********************MAS_MENU******************************/

    ApiResponse<DefaultResponse> createMenu(Long userId, CreateMenuReq request);
    ApiResponse<DefaultResponse> updateMenu(Long userId, CreateMenuReq request);
    ApiResponse<DefaultResponse> updateMenuStatus(Long userId, MenuStatusReq request);
    ApiResponse<MasMenu> getMenuByMenuId(GetMenuReq request);
    ApiResponseWithPageable<List<MasMenu>> getAllMenus(GetMenuReq request);


    //**********************MAS_ROLE******************************/

    ApiResponse<DefaultResponse> createRole(Long userId, CreateRoleReq request);
    ApiResponse<DefaultResponse> updateRole(Long userId, CreateRoleReq request);
    ApiResponse<DefaultResponse> updateRoleStatus(Long userId, RoleStatusReq request);
    ApiResponse<MasRole> getRoleByRoleId(GetRoleReq request);
    ApiResponseWithPageable<List<MasRole>> getAllRole(GetRoleReq request);


    //**********************MAS_SCHEDULE_TYPE******************************/

    ApiResponse<DefaultResponse> createScheduleType(Long userId, CreateScheduleTypeReq request);
    ApiResponse<DefaultResponse> updateScheduleType(Long userId, CreateScheduleTypeReq request);
    ApiResponse<DefaultResponse> updateScheduleTypeStatus(Long userId, ScheduleTypeStatusReq request);
    ApiResponse<MasScheduleType> getScheduleTypeByMenuId(GetScheduleTypeReq request);
    ApiResponseWithPageable<List<MasScheduleType>> getAllScheduleType(GetScheduleTypeReq request);

    //**********************MAS_SURVEY_TYPE******************************/

    ApiResponse<DefaultResponse> createSurveyType(Long userId, CreateSurveyTypeReq request);
    ApiResponse<DefaultResponse> updateSurveyType(Long userId, CreateSurveyTypeReq request);
    ApiResponse<DefaultResponse> updateSurveyTypeStatus(Long userId, SurveyTypeStatusReq request);
    ApiResponse<MasSurveyType> getSurveyTypeByMenuId(GetSurveyTypeReq request);
    ApiResponseWithPageable<List<MasSurveyType>> getAllSurveyType(GetSurveyTypeReq request);


    //**********************MAS_DIVISION******************************/

    ApiResponse<DefaultResponse> createDivision(Long userId, CreateDivisionReq request);
    ApiResponse<DefaultResponse> updateDivision(Long userId, CreateDivisionReq request);
    ApiResponse<DefaultResponse> updateDivisionStatus(Long userId, DivisionStatusReq request);
    ApiResponse<MasDivision> getDivisionByMenuId(GetDivisionReq request);
    ApiResponseWithPageable<List<MasDivision>> getAllDivision(GetDivisionReq request);
}
