package com.mospi.questionare_survey.service;

import com.mospi.questionare_survey.request.*;
import com.mospi.questionare_survey.response.ApiResponse;
import com.mospi.questionare_survey.response.DefaultResponse;

public interface SurveyService {
    ApiResponse<DefaultResponse> createSurveyDetails(Long userId, CreateSurveyReq request);

    ApiResponse<DefaultResponse> createTemplateDetails(Long userId, CreateTemplateReq request);

    ApiResponse<DefaultResponse> createScheduleDetails(Long userId, CreateScheduleReq request);

    ApiResponse<DefaultResponse> createBlockDetails(Long userId, BlockDetailsReq request);

    ApiResponse<DefaultResponse> createQuestionDetails(Long userId, QuestinDetailsReq request);
}
