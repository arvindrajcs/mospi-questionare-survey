package com.mospi.questionare_survey.service;


import com.mospi.questionare_survey.request.AuthRequest;
import com.mospi.questionare_survey.request.TokenRefreshRequest;
import com.mospi.questionare_survey.response.ApiResponse;
import com.mospi.questionare_survey.response.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public interface AuthService {

    ApiResponse<AuthResponse> getLogin(AuthRequest req, HttpServletResponse response, HttpServletRequest request);

    ApiResponse<AuthResponse> getRefresh(TokenRefreshRequest request, HttpServletRequest req);

    ApiResponse<Boolean> getValidateToken(String authHeader);

    ApiResponse<String> getLogout(TokenRefreshRequest request);

    ApiResponse<String> getLogoutAllDevice(TokenRefreshRequest request);


}
