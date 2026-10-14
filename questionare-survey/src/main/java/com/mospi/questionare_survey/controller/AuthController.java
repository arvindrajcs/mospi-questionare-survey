package com.mospi.questionare_survey.controller;

import com.mospi.questionare_survey.request.AuthRequest;
import com.mospi.questionare_survey.request.TokenRefreshRequest;
import com.mospi.questionare_survey.response.ApiResponse;
import com.mospi.questionare_survey.response.AuthResponse;
import com.mospi.questionare_survey.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
@Tag(name = "authController",description = "This controller only for Authentication and Authorization Related task.")
@Slf4j
public class AuthController {

    private final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;


    @Operation(summary = "This API is used to get Token and Refresh Token using user credential.")
    @PostMapping("/authentication/login")
    public ResponseEntity<ApiResponse<AuthResponse>> getLogin(@RequestBody AuthRequest req, HttpServletResponse response, HttpServletRequest request) {
        logger.info("trying to call login token API :{}", req.getUsername());
        return new ResponseEntity<>(authService.getLogin(req,response,request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to get new Refresh Token using user credential.")
    @PostMapping("/authentication/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> getRefresh(@RequestBody TokenRefreshRequest request, HttpServletRequest req) {
        logger.info("trying to call getRefresh token API :{}", request.getDeviceInfo());
        return new ResponseEntity<>(authService.getRefresh(request,req), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to validate token for external micro-service.")
    @PostMapping("/authentication/validate")
    public ResponseEntity<ApiResponse<Boolean>> getValidateToken(@RequestHeader("Authorization") String authHeader) {
        logger.info("trying to call validate token for external micro-service. API ");
        return new ResponseEntity<>(authService.getValidateToken(authHeader), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to Logout Successful from current Device for current users.")
    @PostMapping("/authentication/logout")
    public ResponseEntity<ApiResponse<String>> getLogout(@RequestBody TokenRefreshRequest request) {
        logger.info("trying to call getLogout API :{}", request.getDeviceInfo());
        return new ResponseEntity<>(authService.getLogout(request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to Logout Successful from All Device for current users.")
    @PostMapping("/authentication/logout-all-device")
    public ResponseEntity<ApiResponse<String>> getLogoutAllDevice(@RequestBody TokenRefreshRequest request) {
        logger.info("trying to call getLogoutAllDevice API :{}", request.getDeviceInfo());
        return new ResponseEntity<>(authService.getLogoutAllDevice(request), HttpStatus.OK);
    }


}
