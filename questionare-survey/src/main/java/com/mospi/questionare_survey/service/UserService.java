package com.mospi.questionare_survey.service;

import com.mospi.questionare_survey.request.CreateUserReq;
import com.mospi.questionare_survey.request.GetUserReq;
import com.mospi.questionare_survey.request.UpdateUserReq;
import com.mospi.questionare_survey.request.UserStatusReq;
import com.mospi.questionare_survey.response.ApiResponse;
import com.mospi.questionare_survey.response.ApiResponseWithPageable;
import com.mospi.questionare_survey.response.DefaultResponse;
import com.mospi.questionare_survey.response.UserInfoResp;

import java.util.List;

public interface UserService {

    ApiResponse<DefaultResponse> createUser(Long userId, CreateUserReq request);

    ApiResponse<DefaultResponse> updateUser(Long userId, UpdateUserReq request);

    ApiResponse<DefaultResponse> updateUserStatus(Long userId, UserStatusReq request);

    ApiResponseWithPageable<List<UserInfoResp>> getAllUserWithPage(GetUserReq request);
}
