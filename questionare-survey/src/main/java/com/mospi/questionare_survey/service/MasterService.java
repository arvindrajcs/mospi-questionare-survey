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

    //**********************MAS_ENQUIRY_TYPE******************************/

    ApiResponse<DefaultResponse> createEnquiry(Long userId, CreateEnquiryReq request);
    ApiResponse<DefaultResponse> updateEnquiry(Long userId, CreateEnquiryReq request);
    ApiResponse<DefaultResponse> updateEnquiryStatus(Long userId, EnquiryStatusReq request);
    ApiResponse<MasEnquiryType> getEnquiryByMenuId(GetEnquiryReq request);
    ApiResponseWithPageable<List<MasEnquiryType>> getAllEnquiry(GetEnquiryReq request);

    //**********************MAS_FREQUENCY_TYPE******************************/

    ApiResponse<DefaultResponse> createFrequency(Long userId, CreateFrequencyReq request);
    ApiResponse<DefaultResponse> updateFrequency(Long userId, CreateFrequencyReq request);
    ApiResponse<DefaultResponse> updateFrequencyStatus(Long userId, FrequencyStatusReq request);
    ApiResponse<MasFrequencyType> getFrequencyByMenuId(GetFrequencyReq request);
    ApiResponseWithPageable<List<MasFrequencyType>> getAllFrequency(GetFrequencyReq request);

    //**********************MAS_SEASONALITY_FACTOR******************************/

    ApiResponse<DefaultResponse> createSeasonality(Long userId, CreateSeasonalityReq request);
    ApiResponse<DefaultResponse> updateSeasonality(Long userId, CreateSeasonalityReq request);
    ApiResponse<DefaultResponse> updateSeasonalityStatus(Long userId, SeasonalityStatusReq request);
    ApiResponse<MasSeasonalityFactor> getSeasonalityByMenuId(GetSeasonalityReq request);
    ApiResponseWithPageable<List<MasSeasonalityFactor>> getAllSeasonality(GetSeasonalityReq request);

    //**********************MAS_SCHEDULE_ATTRIBUTES******************************/

    ApiResponse<DefaultResponse> createScheduleAttributes(Long userId, CreateScheduleAttributesReq request);
    ApiResponse<DefaultResponse> updateScheduleAttributes(Long userId, CreateScheduleAttributesReq request);
    ApiResponse<DefaultResponse> updateScheduleAttributesStatus(Long userId, ScheduleAttributesStatusReq request);
    ApiResponse<MasScheduleAttributes> getScheduleAttributesByMenuId(GetScheduleAttributesReq request);
    ApiResponseWithPageable<List<MasScheduleAttributes>> getAllScheduleAttributes(GetScheduleAttributesReq request);

    //**********************MAS_STATUS******************************/

    ApiResponse<DefaultResponse> createStatus(Long userId, CreateStatusReq request);
    ApiResponse<DefaultResponse> updateStatus(Long userId, CreateStatusReq request);
    ApiResponse<DefaultResponse> updateStatusStatus(Long userId, StatusStatusReq request);
    ApiResponse<MasStatus> getStatusByMenuId(GetStatusReq request);
    ApiResponseWithPageable<List<MasStatus>> getAllStatus(GetStatusReq request);

    //**********************MAS_COMPARISON******************************/

    ApiResponse<DefaultResponse> createComparison(Long userId, CreateComparisonReq request);
    ApiResponse<DefaultResponse> updateComparison(Long userId, CreateComparisonReq request);
    ApiResponse<DefaultResponse> updateComparisonStatus(Long userId, ComparisonStatusReq request);
    ApiResponseWithPageable<List<MasComparison>> getComparisonByIdOrUser(GetComparisonReq request);
    ApiResponseWithPageable<List<MasComparison>> getAllComparison(GetComparisonReq request);

    //**********************MAS_VALIDATION******************************/

    ApiResponse<DefaultResponse> createValidation(Long userId, CreateValidationReq request);
    ApiResponse<DefaultResponse> updateValidation(Long userId, CreateValidationReq request);
    ApiResponse<DefaultResponse> updateValidationStatus(Long userId, ValidationStatusReq request);
    ApiResponseWithPageable<List<MasValidation>> getValidationByIdOrUser(GetValidationReq request);
    ApiResponseWithPageable<List<MasValidation>> getAllValidation(GetValidationReq request);
}
