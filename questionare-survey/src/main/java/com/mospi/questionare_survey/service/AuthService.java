package com.mospi.questionare_survey.service;


import java.util.List;

public interface AuthService {

    ApiResponse<DefaultResponse> createUser(Long userId,CreateUserRequest request);

    ApiResponse<DefaultResponse> updateUser(Long userId,UpdateUserRequest request);

    ApiResponse<DefaultResponse> updateUserStatus(Long userId,UserStatusRequest request);

    ApiResponse<DefaultResponse> assignUserRole(Long userId,AssignUserRoleRequest userRequest);

    ApiResponse<DefaultResponse> updateUsersRole(Long userId,UpdateUserRoleRequest request);

    ApiResponse<DefaultResponse> approveUser(Long userId, ApproveRequest request);



    ApiResponseWithPageable<List<UserResponse>> getAllApprovalPendingUsers(Long userId, UserRequest request);

    ApiResponseWithPageable<List<UserResponse>> getAllApprovalRejectedUsers(Long userId,UserRequest request);

    ApiResponseWithPageable<List<UserResponse>> getAllApprovalApprovedUsers(Long userId, UserRequest request);

    ApiResponseWithPageable<List<UserResponse>> getAllUsers(Long userId,UserRequest request);

    ApiResponseWithPageable<List<UserResponse>> getAllInActiveUsers(Long userId,UserRequest request);




    ApiResponseWithPageable<List<UserResponse>> getUsersByOrg(Long userId,GetUserOrgRequest request);

    ApiResponse<?> getOrgTypes(AllOrgRequest request);

    ApiResponse<UserResponse> getUsersById(UserRequest request);


}
