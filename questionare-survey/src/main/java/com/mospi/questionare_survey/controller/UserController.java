package com.mospi.questionare_survey.controller;


import com.mospi.questionare_survey.request.*;
import com.mospi.questionare_survey.response.*;
import com.mospi.questionare_survey.service.UserService;
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
@RequestMapping("/api/v1/user")
@CrossOrigin(origins = "*")
@Tag(name = "UserController",description = "This controller only for User Related task.")
@Slf4j
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Operation(summary = "This API is used to create User with basic Details")
    @PostMapping("/user/create-user")
    public ResponseEntity<ApiResponse<DefaultResponse>> createUser(@RequestHeader("userId") Long userId, @RequestBody CreateUserReq request) {
        logger.info("trying to call createUser API");
        return new ResponseEntity<>(userService.createUser(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to update User with basic Details")
    @PostMapping("/user/update-user")
    public ResponseEntity<ApiResponse<DefaultResponse>> updateUser(@RequestHeader("userId") Long userId,@RequestBody UpdateUserReq request) {
        logger.info("trying to call updateUser API");
        return new ResponseEntity<>(userService.updateUser(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to get user Enable and Disable.")
    @PostMapping("/user/update-status")
    public ResponseEntity<ApiResponse<DefaultResponse>> updateUserStatus(@RequestHeader("userId") Long userId,@RequestBody UserStatusReq request) {
        logger.info("trying to call updateUserStatus API");
        return new ResponseEntity<>(userService.updateUserStatus(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to Get All Users List or By UserId")
    @PostMapping("/user/get-user")
    public ResponseEntity<ApiResponseWithPageable<List<UserInfoResp>>> getAllUserWithPage(@RequestBody GetUserReq request) {
        logger.info("Trying to call getAllUserWithPage");
        return new ResponseEntity<>(userService.getAllUserWithPage(request), HttpStatus.OK
        );
    }
}
