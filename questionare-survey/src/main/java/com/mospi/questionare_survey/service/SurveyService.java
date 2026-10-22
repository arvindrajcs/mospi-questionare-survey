package com.mospi.questionare_survey.service;

import com.mospi.questionare_survey.entity.*;
import com.mospi.questionare_survey.request.*;
import com.mospi.questionare_survey.response.*;

import java.util.List;

public interface SurveyService {

    //**********************SURVEY DETAILS******************************/

    ApiResponse<DefaultResponse> createSurveyDetails(Long userId, CreateSurveyReq request);
    ApiResponse<DefaultResponse> updateSurveyDetails(Long userId, UpdateSurveyReq request);
    ApiResponse<DefaultResponse> updateSurveyStatus(Long userId, SurveyStatusReq request);
    ApiResponseWithPageable<List<SurveyDetails>> getSurveyDetailsById(GetSurveyReq request);
    ApiResponseWithPageable<List<SurveyDetails>> getAllSurveyDetails(GetSurveyReq request);

    //**********************TEMPLATE DETAILS******************************/

    ApiResponse<DefaultResponse> createTemplateDetails(Long userId, CreateTemplateReq request);
    ApiResponse<DefaultResponse> updateTemplateDetails(Long userId, UpdateTemplateReq request);
    ApiResponse<DefaultResponse> updateTemplateStatus(Long userId, TemplateStatusReq request);
    ApiResponseWithPageable<List<TemplateDetails>> getTemplateDetailsById(GetTemplateReq request);
    ApiResponseWithPageable<List<TemplateDetails>> getAllTemplateDetails(GetTemplateReq request);

    //**********************SCHEDULE DETAILS******************************/

    ApiResponse<DefaultResponse> createScheduleDetails(Long userId, CreateScheduleReq request);
    ApiResponse<DefaultResponse> updateScheduleDetails(Long userId, UpdateScheduleReq request);
    ApiResponse<DefaultResponse> updateScheduleStatus(Long userId, ScheduleStatusReq request);
    ApiResponseWithPageable<List<ScheduleDetails>> getScheduleDetailsByIdOrUser(GetScheduleReq request);
    ApiResponseWithPageable<List<ScheduleDetails>> getAllScheduleDetails(GetScheduleReq request);

    //**********************BLOCK DETAILS******************************/

    ApiResponse<DefaultResponse> createBlockDetails(Long userId, BlockDetailsReq request);
    ApiResponse<DefaultResponse> updateBlockDetails(Long userId, UpdateBlockReq request);
    ApiResponse<DefaultResponse> updateBlockStatus(Long userId, BlockStatusReq request);
    ApiResponseWithPageable<List<BlockResp>> getBlockDetailsByIdOrUser(GetBlockReq request);
    ApiResponseWithPageable<List<BlockResp>> getAllBlockDetails(GetBlockReq request);

    //**********************QUESTION DETAILS******************************/

    ApiResponse<DefaultResponse> createQuestionDetails(Long userId, QuestinDetailsReq request);
    ApiResponse<DefaultResponse> updateQuestionDetails(Long userId, UpdateQuestionReq request);
    ApiResponse<DefaultResponse> updateQuestionStatus(Long userId, QuestionStatusReq request);
    ApiResponseWithPageable<List<QuestionResp>> getQuestionDetailsByIdOrUser(GetQuestionReq request);
    ApiResponseWithPageable<List<QuestionDetails>> getAllQuestionDetails(GetQuestionReq request);


}
