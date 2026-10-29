package com.mospi.questionare_survey.service.Impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mospi.questionare_survey.entity.UserInfo;
import com.mospi.questionare_survey.exception.SDDException;
import com.mospi.questionare_survey.helperUtil.ErrorCode;
import com.mospi.questionare_survey.helperUtil.HelperUtils;
import com.mospi.questionare_survey.helperUtil.ResponseUtils;
import com.mospi.questionare_survey.repository.MappingUserRoleRepo;
import com.mospi.questionare_survey.repository.UserInfoRepo;
import com.mospi.questionare_survey.request.CreateUserReq;
import com.mospi.questionare_survey.request.GetUserReq;
import com.mospi.questionare_survey.request.UpdateUserReq;
import com.mospi.questionare_survey.request.UserStatusReq;
import com.mospi.questionare_survey.response.ApiResponse;
import com.mospi.questionare_survey.response.ApiResponseWithPageable;
import com.mospi.questionare_survey.response.DefaultResponse;
import com.mospi.questionare_survey.response.UserInfoResp;
import com.mospi.questionare_survey.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private HelperUtils helperUtill;

    @Autowired
    private UserInfoRepo userInfoRepo;

    @Autowired
    private MappingUserRoleRepo mappingUserRoleRepo;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<DefaultResponse> createUser(Long userId, CreateUserReq request) {

        try {
            UserInfo user = new UserInfo();
            String encryptedPassword = passwordEncoder.encode(request.getPasswordHash());
            user.setUsername(request.getUsername());
            user.setPasswordHash(encryptedPassword);
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setEmail(request.getEmail());
            user.setMobileNumber(request.getMobileNumber());
            user.setCreatedBy(userId);
            userInfoRepo.save(user);
            DefaultResponse response = new DefaultResponse();
            response.setMsg("User created successfully.Approval pending");
            return ResponseUtils.createSuccessResponse(response, new TypeReference<DefaultResponse>() {});
        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while creating user", e);
            throw new SDDException(false, "Unexpected error during user creation.");
        }
    }



    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<DefaultResponse> updateUser(Long userId, UpdateUserReq request) {

        try {
            UserInfo existingUser;
            if (request.getUserId() != null) {
                existingUser = userInfoRepo.findById(request.getUserId())
                        .orElseThrow(() -> new SDDException(false, ErrorCode.AU1003));
            }else {
                throw new SDDException(false, ErrorCode.AU1004);
            }
            if (request.getPasswordHash() != null) {
                String encryptedPassword = passwordEncoder.encode(request.getPasswordHash());
                existingUser.setPasswordHash(encryptedPassword);
            }
            Optional.ofNullable(request.getFirstName())
                    .ifPresent(existingUser::setFirstName);

            Optional.ofNullable(request.getLastName())
                    .ifPresent(existingUser::setLastName);

            Optional.ofNullable(request.getEmail())
                    .ifPresent(existingUser::setEmail);

            Optional.ofNullable(request.getMobileNumber())
                    .ifPresent(existingUser::setMobileNumber);

            existingUser.setUpdatedBy(userId);
            userInfoRepo.save(existingUser);
            DefaultResponse response = new DefaultResponse();
            response.setMsg("User updated successfully");
            return ResponseUtils.createSuccessResponse(response, new TypeReference<DefaultResponse>() {});
        }
        catch (SDDException e) {
            throw e;
        }
        catch (Exception e) {
            logger.error("Error while updating user", e);
            throw new SDDException(false, "Unexpected error during user update.");
        }
    }



    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<DefaultResponse> updateUserStatus(Long userId, UserStatusReq request) {
        try {

            UserInfo existingUser = userInfoRepo.findById(request.getUserId())
                    .orElseThrow(() -> new SDDException(false, ErrorCode.AU1003));

            existingUser.setIsActive(request.getActive());
            existingUser.setUpdatedBy(userId);
            userInfoRepo.save(existingUser);

            String message = request.getActive() ? "User activated successfully" : "User deactivated successfully";
            logger.info("User status updated successfully for userId: {}", request.getUserId());
            DefaultResponse defaultResponse = new DefaultResponse(message);
            return ResponseUtils.createSuccessResponse(defaultResponse);

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating User-Role Mapped status for userId: {}", userId, e);
            throw new SDDException(false, "Unexpected error during User-Role Mapped status updating");
        }
    }

    @Override
    public ApiResponseWithPageable<List<UserInfoResp>> getAllUserWithPage(GetUserReq request) {
        logger.info("Fetching User Details. UserId: {}, RollId: {}, Page: {}, Limit: {}",
                request.getUserId(), request.getRollId(), request.getPage(), request.getLimit());

        try {
            Pageable pageable = helperUtill.getPageable(request.getPage(), request.getLimit());
            List<UserInfoResp> userInfoRespList = new ArrayList<>();
            long totalElements = 0;
            int totalPages = 0;

            if (request.getUserId() != null) {
                // Scenario 1: If userId is provided, fetch only that specific user
                UserInfo user = userInfoRepo.findById(request.getUserId())
                        .orElseThrow(() -> new SDDException(false, "User not found for ID: " + request.getUserId()));

                userInfoRespList.add(mapToUserInfoResp(user));
                totalElements = 1;
                totalPages = 1;

            } else if (request.getRollId() != null) {
                // Scenario 2: If rollId is provided (and userId is null), fetch users by roleId with pagination
                Page<UserInfo> userPage = mappingUserRoleRepo.findUsersByRoleId(request.getRollId(), pageable);
                totalElements = userPage.getTotalElements();
                totalPages = userPage.getTotalPages();

                for (UserInfo user : userPage.getContent()) {
                    userInfoRespList.add(mapToUserInfoResp(user));
                }

            } else {
                // Scenario 3: If neither userId nor rollId is provided, paginate through all users
                Page<UserInfo> userPage = userInfoRepo.findAll(pageable);
                totalElements = userPage.getTotalElements();
                totalPages = userPage.getTotalPages();

                for (UserInfo user : userPage.getContent()) {
                    userInfoRespList.add(mapToUserInfoResp(user));
                }
            }

            logger.info("User details fetched successfully");
            return ResponseUtils.createPageableSuccessResponse(
                    userInfoRespList,
                    totalElements,
                    totalPages,
                    pageable.getPageNumber() + 1,
                    pageable.getPageSize()
            );

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching user details", e);
            throw new SDDException(false, "Unexpected error during fetching user details");
        }
    }

    private UserInfoResp mapToUserInfoResp(UserInfo user) {
        UserInfoResp resp = new UserInfoResp();
        resp.setUserId(user.getUserId());
        resp.setUsername(user.getUsername());
        resp.setPasswordHash(user.getPasswordHash());
        resp.setFirstName(user.getFirstName());
        resp.setLastName(user.getLastName());
        resp.setEmail(user.getEmail());
        resp.setMobileNumber(user.getMobileNumber());
        resp.setIsActive(user.getIsActive());
        return resp;
    }

}
