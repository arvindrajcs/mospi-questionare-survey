package com.mospi.questionare_survey.service;

import com.mospi.questionare_survey.request.CreateSurveyReq;
import com.mospi.questionare_survey.response.ApiResponse;
import com.mospi.questionare_survey.response.DefaultResponse;

public interface SurveyService {
    ApiResponse<DefaultResponse> createSurveyDesign(Long userId, CreateSurveyReq request);
}
