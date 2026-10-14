package com.mospi.questionare_survey.service.Impl;



import com.mospi.questionare_survey.request.AuthRequest;
import com.mospi.questionare_survey.request.TokenRefreshRequest;
import com.mospi.questionare_survey.response.ApiResponse;
import com.mospi.questionare_survey.response.AuthResponse;
import com.mospi.questionare_survey.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {


    @Override
    public ApiResponse<AuthResponse> getLogin(AuthRequest req, HttpServletResponse response, HttpServletRequest request) {
        return null;
    }

    @Override
    public ApiResponse<AuthResponse> getRefresh(TokenRefreshRequest request, HttpServletRequest req) {
        return null;
    }

    @Override
    public ApiResponse<Boolean> getValidateToken(String authHeader) {
        return null;
    }

    @Override
    public ApiResponse<String> getLogout(TokenRefreshRequest request) {
        return null;
    }

    @Override
    public ApiResponse<String> getLogoutAllDevice(TokenRefreshRequest request) {
        return null;
    }
}
