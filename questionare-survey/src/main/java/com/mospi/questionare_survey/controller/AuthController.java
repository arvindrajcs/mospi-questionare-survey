package com.mospi.questionare_survey.controller;

import com.mospi.questionare_survey.service.AuthService;
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
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
@Tag(name = "authController",description = "This controller only for Authentication and Authorization Related task.")
@Slf4j
public class AuthController {

    private final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

//    @Operation(summary = "This API is used to create User with basic Details")
//    @PostMapping("/user/create-user")
//    public ResponseEntity<ApiResponse<DefaultResponse>> createUser(@RequestHeader("userId") Long userId, @RequestBody CreateUserRequest request) {
//        logger.info("trying to call createUser API");
//        return new ResponseEntity<>(usersService.createUser(userId, request), HttpStatus.OK);
//    }
//
//    @Operation(summary = "This API is used to update User with basic Details")
//    @PostMapping("/user/update-user")
//    public ResponseEntity<ApiResponse<DefaultResponse>> updateUser(@RequestHeader("userId") Long userId,@RequestBody UpdateUserRequest request) {
//        logger.info("trying to call updateUser API");
//        return new ResponseEntity<>(usersService.updateUser(userId, request), HttpStatus.OK);
//    }
//
//    @Operation(summary = "This API is used to get user Enable and Disable.")
//    @PostMapping("/user/update-status")
//    public ResponseEntity<ApiResponse<DefaultResponse>> updateUserStatus(@RequestHeader("userId") Long userId,@RequestBody UserStatusRequest request) {
//        logger.info("trying to call updateUserStatus API");
//        return new ResponseEntity<>(usersService.updateUserStatus(userId, request), HttpStatus.OK);
//    }
//
//
//    @Operation(summary = "This API is used to assign User Role.")
//    @PostMapping("/user/assign-role")
//    public ResponseEntity<ApiResponse<DefaultResponse>> assignUserRole(@RequestHeader("userId") Long userId,@RequestBody AssignUserRoleRequest userRequest) {
//        logger.info("trying to call assignUserRole API");
//        return new ResponseEntity<>(usersService.assignUserRole(userId, userRequest), HttpStatus.OK);
//    }
//
//
//    @Operation(summary = "This API is used to update user User Role and User Department.")
//    @PostMapping("/user/update-role")
//    public ResponseEntity<ApiResponse<DefaultResponse>> updateUsersRole(@RequestHeader("userId") Long userId,@RequestBody UpdateUserRoleRequest request) {
//        logger.info("trying to call updateUsersRole API");
//        return new ResponseEntity<>(usersService.updateUsersRole(userId, request), HttpStatus.OK);
//    }
//
//
//    @Operation(summary = "This API is used to Approve user .")
//    @PostMapping("/user/approve-user")
//    public ResponseEntity<ApiResponse<DefaultResponse>> approveUser(@RequestHeader("userId") Long userId, @RequestBody ApproveRequest request) {
//        logger.info("trying to call approveUser API");
//        return new ResponseEntity<>(usersService.approveUser(userId, request), HttpStatus.OK);
//    }
//
//    @Operation(summary = "This API is used to get All Approval Pending Users List.")
//    @PostMapping("/user/approval-pending")
//    public ResponseEntity<ApiResponseWithPageable<List<UserResponse>>> getAllApprovalPendingUsers(@RequestHeader("userId") Long userId, @RequestBody UserRequest request) {
//        logger.info("Trying to call getAllApprovalPendingUsers");
//        return new ResponseEntity<>(usersService.getAllApprovalPendingUsers(userId, request), HttpStatus.OK
//        );
//    }
//
//    @Operation(summary = "This API is used to get All Approval Reject Users List.")
//    @PostMapping("/user/approval-reject")
//    public ResponseEntity<ApiResponseWithPageable<List<UserResponse>>> getAllApprovalRejectedUsers(@RequestHeader("userId") Long userId, @RequestBody UserRequest request) {
//        logger.info("Trying to call getAllApprovalRejectedUsers");
//        return new ResponseEntity<>(usersService.getAllApprovalRejectedUsers(userId, request), HttpStatus.OK
//        );
//    }
//
//    @Operation(summary = "This API is used to get All Active Users List.")
//    @PostMapping("/user/approval-approved")
//    public ResponseEntity<ApiResponseWithPageable<List<UserResponse>>> getAllApprovalApprovedUsers(@RequestHeader("userId") Long userId, @RequestBody UserRequest request) {
//        logger.info("Trying to call getAllActiveUsers");
//        return new ResponseEntity<>(usersService.getAllApprovalApprovedUsers(userId, request), HttpStatus.OK
//        );
//    }
//
//    @Operation(summary = "This API is used to get All List Users.")
//    @PostMapping("/user/users-all")
//    public ResponseEntity<ApiResponseWithPageable<List<UserResponse>>> getAllUsers(@RequestHeader("userId") Long userId, @RequestBody UserRequest request) {
//        logger.info("Trying to call getAllUsers");
//        return new ResponseEntity<>(usersService.getAllUsers(userId, request), HttpStatus.OK
//        );
//    }
//
//
//    @Operation(summary = "This API is used to get Users by organization type and organization id .")
//    @PostMapping("/user/user-orgId")
//    public ResponseEntity<ApiResponseWithPageable<List<UserResponse>>> getUsersByOrg(@RequestHeader("userId") Long userId, @RequestBody GetUserOrgRequest request) {
//        logger.info("Trying to call getUsersByOrg");
//        return new ResponseEntity<>(usersService.getUsersByOrg(userId, request), HttpStatus.OK
//        );
//    }
//
//    @Operation(summary = "This API is used to get User by user id or login id.")
//    @PostMapping("/user/user-loginId")
//    public ResponseEntity<ApiResponse<?>> getUsersById(@RequestHeader("userId") Long userId, @RequestBody UserRequest request) {
//        logger.info("trying to call getUsersById API");
//        return new ResponseEntity<>(usersService.getUsersById(request), HttpStatus.OK);
//    }
//
//
//    @Operation(summary = "This API is used to get All District List of Rajasthan.")
//    @PostMapping("/user/get-org-type")
//    public ResponseEntity<ApiResponse<?>> getOrgTypes(@RequestBody AllOrgRequest request) {
//        logger.info("trying to call getOrgTypes API");
//        return new ResponseEntity<>(usersService.getOrgTypes(request), HttpStatus.OK);
//    }
//
//
//    @Operation(summary = "This API is used to get All In-Active Users List.")
//    @PostMapping("/user/inActive-users")
//    public ResponseEntity<ApiResponseWithPageable<List<UserResponse>>> getAllInActiveUsers(@RequestHeader("userId") Long userId, @RequestBody UserRequest request) {
//        logger.info("Trying to call getAllInActiveUsers");
//        return new ResponseEntity<>(usersService.getAllInActiveUsers(userId, request), HttpStatus.OK
//        );
//    }



}
