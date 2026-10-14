package com.mospi.questionare_survey.service.Impl;


import com.fasterxml.jackson.core.type.TypeReference;
import com.mospi.questionare_survey.service.AuthService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class AuthServiceImpl implements AuthService {


    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<DefaultResponse> createUser(Long userId,CreateUserRequest request) {
        ValidationUtil.validateCreateUserRequest(request);
        try {
            AppUser userObj= validationUtilClass.getUserDetails(userId);
            if(userObj.getRole() == null || userObj.getRole().getDescription() == null || !userObj.getRole().getDescription().equalsIgnoreCase("Super-Admin")){
                throw new SDDException(false, ErrorCode.AU1013);
            }

//            byte[] aesKey = helperUtill.decryptAESKey(request.getKey());
//
//            String loginId = helperUtill.decryptData(request.getLoginId(), aesKey);
//            String password = helperUtill.decryptData(request.getPassword(), aesKey);


            String loginId = request.getLoginId();
            String password = request.getPassword();

            if (!password.matches(PASSWORD_REGEX)) {
                throw new SDDException(false,
                        "Password must have minimum 7 characters, 1 uppercase letter, 1 number, and 1 special symbol");
            }
            // Unique LoginId check
            if (loginId != null &&
                    userRepo.findByLoginIdIgnoreCase(loginId).isPresent()) {
                throw new SDDException(false, ErrorCode.AU1016);
            }
            // Unique Mobile check
            if (request.getMobile() != null &&
                    userRepo.findByMobile(request.getMobile()).isPresent()) {
                throw new SDDException(false, ErrorCode.AU1017);
            }
            // Unique Email check
            if (request.getEmail() != null &&
                    userRepo.findByEmailIgnoreCase(request.getEmail()).isPresent()) {
                throw new SDDException(false, ErrorCode.AU1018);
            }
            // Copy Request → Entity
            AppUser appUser = new AppUser();

            if (request.getOrgType() != null && request.getOrgType().equalsIgnoreCase("HOSPITAL") ) {

                HospitalMaster hospitalObj = hospitalMasterRepository.findById(request.getOrgId())
                        .orElseThrow(() -> new SDDException(false, ErrorCode.AU1025));
                appUser.setHospital(hospitalObj);

            } else if (request.getOrgType() != null && request.getOrgType().equalsIgnoreCase("TPA") ) {

                TpaMaster tpaObj = tpaRepository.findById(request.getOrgId())
                        .orElseThrow(() -> new SDDException(false, ErrorCode.AU1024));
                appUser.setTpa(tpaObj);

            } else if (request.getOrgType() != null && request.getOrgType().equalsIgnoreCase("INSURANCE") ) {

                InsuranceMaster insuranceObj = insuranceRepository.findById(request.getOrgId())
                        .orElseThrow(() -> new SDDException(false, ErrorCode.AU1023));
                appUser.setInsurance(insuranceObj);

            } else if (request.getOrgType() != null && request.getOrgType().equalsIgnoreCase("DISTRICT") ) {

                DistrictMaster districtObj = districtMasterRepository.findById(request.getOrgId())
                        .orElseThrow(() -> new SDDException(false, ErrorCode.AU1022));
                appUser.setDistrict(districtObj);

            } else if (request.getOrgType() != null && request.getOrgType().equalsIgnoreCase("RSHAA") ) {

                GovOrgMaster govObj = govOrgRepository.findById(request.getOrgId())
                        .orElseThrow(() -> new SDDException(false, ErrorCode.AU1026));
                appUser.setGovOrg(govObj);

            }else {
                throw new SDDException(false, "User must belong to exactly one organization");
            }

            RoleMaster roleObj = roleMasterRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new SDDException(false, ErrorCode.AU1015));



            String encryptedPassword = passwordEncoder.encode(password);
            appUser.setFirstName(request.getFirstName());
            appUser.setLastName(request.getLastName());
            appUser.setPassword(encryptedPassword);
            appUser.setRole(roleObj);
            appUser.setLoginId(loginId);
            appUser.setEmail(request.getEmail());
            appUser.setMobile(request.getMobile());
            appUser.setRemarks(request.getRemarks());
            appUser.setDocPath(request.getDocPath());
            appUser.setIsActive(0);
            appUser.setCreatedBy(userObj);
            appUser.setCreatedDate(LocalDateTime.now());
            userRepo.save(appUser);
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
    public ApiResponse<DefaultResponse> updateUser(Long userId, UpdateUserRequest request) {

        try {
            AppUser userObj =
                    validationUtilClass.getUserDetails(userId);

            // Allow only Super Admin
            if (userObj.getRole() == null ||
                    userObj.getRole().getDescription() == null ||
                    !userObj.getRole()
                            .getDescription()
                            .equalsIgnoreCase("Super-Admin")) {

                throw new SDDException(false, ErrorCode.AU1013);
            }

            // Fetch existing user
            AppUser appUser =
                    userRepo.findById(request.getUserId())
                            .orElseThrow(() ->
                                    new SDDException(false, ErrorCode.AU1014));

            // Mobile unique check
            if (request.getMobile() != null &&
                    userRepo.findByMobile(request.getMobile())
                            .filter(user ->
                                    !user.getUserId()
                                            .equals(request.getUserId()))
                            .isPresent()) {

                throw new SDDException(false, ErrorCode.AU1017);
            }

            // Email unique check
            if (request.getEmail() != null &&
                    userRepo.findByEmailIgnoreCase(request.getEmail())
                            .filter(user ->
                                    !user.getUserId()
                                            .equals(request.getUserId()))
                            .isPresent()) {

                throw new SDDException(false, ErrorCode.AU1018);
            }

            if (request.getRoleId() != null) {

                RoleMaster roleObj =
                        roleMasterRepository
                                .findById(request.getRoleId())
                                .orElseThrow(() ->
                                        new SDDException(false, ErrorCode.AU1015));

                appUser.setRole(roleObj);
            }

            Optional.ofNullable(request.getFirstName())
                    .ifPresent(appUser::setFirstName);

            Optional.ofNullable(request.getLastName())
                    .ifPresent(appUser::setLastName);

            Optional.ofNullable(request.getEmail())
                    .ifPresent(appUser::setEmail);

            Optional.ofNullable(request.getMobile())
                    .ifPresent(appUser::setMobile);

            Optional.ofNullable(request.getRemarks())
                    .ifPresent(appUser::setRemarks);

            Optional.ofNullable(request.getDocPath())
                    .ifPresent(appUser::setDocPath);


            appUser.setUpdatedBy(userObj);
            appUser.setUpdatedDate(LocalDateTime.now());

            userRepo.save(appUser);

            DefaultResponse response =
                    new DefaultResponse();

            response.setMsg("User updated successfully");

            return ResponseUtils.createSuccessResponse(
                    response,
                    new TypeReference<DefaultResponse>() {}
            );

        }

        catch (SDDException e) {
            throw e;
        }

        catch (Exception e) {

            logger.error("Error while updating user", e);

            throw new SDDException(
                    false,
                    "Unexpected error during user update."
            );
        }
    }



    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<DefaultResponse> updateUserStatus(Long userId,UserStatusRequest request) {
        ValidationUtil.validateUpdateUserStatusRequest(request);
        try {
            AppUser userObj= validationUtilClass.getUserDetails(userId);
            if(userObj.getRole() == null || userObj.getRole().getDescription() == null || !userObj.getRole().getDescription().equalsIgnoreCase("Super-Admin")){
                throw new SDDException(false, ErrorCode.AU1013);
            }

            // Fetch existing user (by userId or loginId)
            AppUser appUser;
            if (request.getUserId() != null) {
                appUser = userRepo.findById(request.getUserId())
                        .orElseThrow(() -> new SDDException(false, ErrorCode.AU1011));
            } else if (request.getLoginId() != null && !request.getLoginId().isEmpty()) {
                appUser = userRepo.findByLoginIdIgnoreCase(request.getLoginId())
                        .orElseThrow(() -> new SDDException(false, ErrorCode.AU1011));
            } else {
                throw new SDDException(false, ErrorCode.AU1012);
            }
            String action ="";
            if (request.getFlag() == 0) {
                action ="Deactivation";
                appUser.setIsActive(0);
            } else if (request.getFlag() == 1) {
                action ="Activation";
                appUser.setIsActive(0);
            } else {
                throw new SDDException(false, ErrorCode.AU1020);
            }
            appUser.setApprovedFlag(0);
            appUser.setDocPath(request.getDocPath());
            appUser.setUpdatedBy(userObj);
            appUser.setUpdatedDate(LocalDateTime.now());
            userRepo.save(appUser);

            DefaultResponse response = new DefaultResponse();
            response.setMsg("User " + action + " request submitted. Approval pending.");
            return ResponseUtils.createSuccessResponse(
                    response,
                    new TypeReference<DefaultResponse>() {}
            );
        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating user status", e);
            throw new SDDException(false, "Unexpected error during user status update.");
        }
    }




    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<DefaultResponse> assignUserRole(Long userId,AssignUserRoleRequest request) {

        ValidationUtil.validateAssignUserRoleRequest(request);

        try {
            AppUser userObj= validationUtilClass.getUserDetails(userId);
            if(userObj.getRole() == null || userObj.getRole().getDescription() == null || !userObj.getRole().getDescription().equalsIgnoreCase("Super-Admin")){
                throw new SDDException(false, ErrorCode.AU1013);
            }
            // Fetch existing user (by userId or loginId)
            AppUser appUser;

            if (request.getUserId() != null) {
                appUser = userRepo.findById(request.getUserId())
                        .orElseThrow(() -> new SDDException(false, ErrorCode.AU1011));
            } else if (request.getLoginId() != null && !request.getLoginId().isEmpty()) {
                appUser = userRepo.findByLoginIdIgnoreCase(request.getLoginId())
                        .orElseThrow(() -> new SDDException(false, ErrorCode.AU1011));
            } else {
                throw new SDDException(false, ErrorCode.AU1012);
            }

            // Fetch & Assign Role
            if (request.getRoleId() != null) {
                RoleMaster roleObj = roleMasterRepository.findById(request.getRoleId())
                        .orElseThrow(() -> new SDDException(false, ErrorCode.AU1015));
                appUser.setRole(roleObj);
            }

            // Status & Approval Flow
            appUser.setIsActive(0);
            appUser.setApprovedFlag(0);
            appUser.setDocPath(request.getDocPath());
            appUser.setUpdatedBy(userObj);
            appUser.setUpdatedDate(LocalDateTime.now());
            userRepo.save(appUser);

            DefaultResponse response = new DefaultResponse();
            response.setMsg("Role assigned successfully. Approval pending.");
            return ResponseUtils.createSuccessResponse(
                    response,
                    new TypeReference<DefaultResponse>() {}
            );

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while assigning role to user", e);
            throw new SDDException(false, "Unexpected error during role assignment.");
        }
    }




    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<DefaultResponse> updateUsersRole(Long userId,UpdateUserRoleRequest request) {
        ValidationUtil.validateUpdateUsersRoleRequest(request);
        try {
            AppUser userObj= validationUtilClass.getUserDetails(userId);
            if(userObj.getRole() == null || userObj.getRole().getDescription() == null || !userObj.getRole().getDescription().equalsIgnoreCase("Super-Admin")){
                throw new SDDException(false, ErrorCode.AU1013);
            }

            // Fetch existing user (by userId or loginId)
            AppUser appUser;
            if (request.getUserId() != null) {
                appUser = userRepo.findById(request.getUserId())
                        .orElseThrow(() -> new SDDException(false, ErrorCode.AU1011));
            } else if (request.getLoginId() != null && !request.getLoginId().isEmpty()) {
                appUser = userRepo.findByLoginIdIgnoreCase(request.getLoginId())
                        .orElseThrow(() -> new SDDException(false, ErrorCode.AU1011));
            } else {
                throw new SDDException(false, ErrorCode.AU1012);
            }
            // Validate previous role
            if (appUser.getRole() == null ||
                    !appUser.getRole().getRoleId().equals(request.getPreviousRoleId())) {
                throw new SDDException(false, ErrorCode.AU1019);
            }
            String oldRole=appUser.getRole().getDescription();
            // Fetch & Assign new role
            RoleMaster newRoleObj = roleMasterRepository.findById(request.getNewRoleId())
                    .orElseThrow(() -> new SDDException(false, ErrorCode.AU1025));

            if (oldRole.equalsIgnoreCase(newRoleObj.getDescription())) {
                throw new SDDException(false,  "Old Role and New Role Both are Same");
            }

            appUser.setRole(newRoleObj);
            //Approval & audit flow
            appUser.setIsActive(0);          // InActive
            appUser.setApprovedFlag(0);   // Pending
            appUser.setDocPath(request.getDocPath());
            appUser.setUpdatedBy(userObj);
            appUser.setUpdatedDate(LocalDateTime.now());
            userRepo.save(appUser);
            DefaultResponse response = new DefaultResponse();
            response.setMsg("User role updated from "
                    + oldRole
                    + " to "
                    + newRoleObj.getDescription()
                    + ". Approval pending."
            );
            return ResponseUtils.createSuccessResponse(
                    response,
                    new TypeReference<DefaultResponse>() {}
            );
        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating user role", e);
            throw new SDDException(false, "Unexpected error during user role update.");
        }
    }




    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<DefaultResponse> approveUser(Long userId, ApproveRequest request) {
        logger.info("Approving user. userId: {}, status: {}",
                request.getUserId(), request.getFlag());
        AppUser userObj= validationUtilClass.getUserDetails(userId);
        if(userObj.getRole() == null || userObj.getRole().getDescription() == null || !userObj.getRole().getDescription().equalsIgnoreCase("Super-Admin")){
            throw new SDDException(false, ErrorCode.AU1013);
        }

        try {
            if (request.getUserId() == null) {
                throw new SDDException(false, ErrorCode.AU1012);
            }
            AppUser user = userRepo.findByUserIdAndIsActiveAndApprovedFlag(request.getUserId(),0,0)
                    .orElseThrow(() -> new SDDException(false, ErrorCode.AU1010));

            String action="";
            if (request.getFlag() == 1) {
                action="Approved";
                user.setIsActive(1);
                user.setApprovedFlag(1);
                user.setRemarks("Approved: "+request.getRemarks());
            } else if (request.getFlag() == 2) {
                action="Rejected";
                user.setIsActive(0);
                user.setApprovedFlag(2);
                user.setRemarks("Rejected: "+request.getRemarks());
            } else {
                throw new SDDException(false, ErrorCode.AU1021);
            }
            user.setApprovedBy(userObj);
            user.setApprovedDate(LocalDateTime.now());
            userRepo.save(user);

            DefaultResponse response = new DefaultResponse();
            response.setMsg("User " + action + " successfully.");
            return ResponseUtils.createSuccessResponse(
                    response,
                    new TypeReference<DefaultResponse>() {}
            );
        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while approving user", e);
            throw new SDDException(false, "Unexpected error while approving user");
        }
    }



    @Override
    public ApiResponseWithPageable<List<UserResponse>> getAllApprovalPendingUsers(Long userId, UserRequest request) {
        logger.info("Fetching APPROVAL PENDING users. Page: {}, Limit: {}",
                request.getPage(), request.getLimit());
        try {

            Pageable pageable = helperUtill.getPageable(
                    request.getPage(),
                    request.getLimit()
            );
            Page<AppUser> usersPage = userRepo.findByIsActiveAndApprovedFlag(0,0, pageable);
            List<UserResponse> responseList = usersPage.getContent()
                    .stream()
                    .map(helperUtill::mapToUserResponse)
                    .collect(Collectors.toList());
            return ResponseUtils.createPageableSuccessResponse(
                    responseList,
                    usersPage.getTotalElements(),
                    usersPage.getTotalPages(),
                    pageable.getPageNumber() + 1,
                    pageable.getPageSize()
            );
        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching approval pending users", e);
            throw new SDDException(false, "Unexpected error while fetching approval pending users");
        }
    }



    @Override
    public ApiResponseWithPageable<List<UserResponse>> getAllApprovalRejectedUsers(Long userId,UserRequest request) {
        logger.info("Fetching APPROVAL REJECTED users. Page: {}, Limit: {}",
                request.getPage(), request.getLimit());
        try {
            AppUser userObj= validationUtilClass.getUserDetails(userId);
            Pageable pageable = helperUtill.getPageable(
                    request.getPage(),
                    request.getLimit()
            );
            Page<AppUser> usersPage = userRepo.findByIsActiveAndApprovedFlag(0,2, pageable);
            List<UserResponse> responseList = usersPage.getContent()
                    .stream()
                    .map(helperUtill::mapToUserResponse)
                    .collect(Collectors.toList());
            return ResponseUtils.createPageableSuccessResponse(
                    responseList,
                    usersPage.getTotalElements(),
                    usersPage.getTotalPages(),
                    pageable.getPageNumber() + 1,
                    pageable.getPageSize()
            );
        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching approval rejected users", e);
            throw new SDDException(false, "Unexpected error while fetching approval rejected users");
        }
    }




    @Override
    public ApiResponseWithPageable<List<UserResponse>> getAllApprovalApprovedUsers(Long userId,UserRequest request) {
        logger.info("Fetching APPROVAL APPROVED users. Page: {}, Limit: {}",
                request.getPage(), request.getLimit());
        try {
            AppUser userObj= validationUtilClass.getUserDetails(userId);
            Pageable pageable = helperUtill.getPageable(
                    request.getPage(),
                    request.getLimit()
            );
            Page<AppUser> usersPage = userRepo.findByIsActiveAndApprovedFlag(1,1, pageable);
            List<UserResponse> responseList = usersPage.getContent()
                    .stream()
                    .map(helperUtill::mapToUserResponse)
                    .collect(Collectors.toList());
            return ResponseUtils.createPageableSuccessResponse(
                    responseList,
                    usersPage.getTotalElements(),
                    usersPage.getTotalPages(),
                    pageable.getPageNumber() + 1,
                    pageable.getPageSize()
            );
        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching approval approved users", e);
            throw new SDDException(false, "Unexpected error while fetching approval approved users");
        }
    }



    @Override
    public ApiResponseWithPageable<List<UserResponse>> getAllUsers(
            Long userId,
            UserRequest request) {

        logger.info("Fetching users. Page: {}, Limit: {}",
                request.getPage(),
                request.getLimit());

        try {

            // Validate logged-in user
            AppUser userObj =
                    validationUtilClass.getUserDetails(userId);

            // Pageable
            Pageable pageable =
                    helperUtill.getPageable(
                            request.getPage(),
                            request.getLimit()
                    );

            Page<AppUser> usersPage;

        /*
         ===============================
         FILTER LOGIC
         ===============================
         */

            if (request.getUserId() != null) {

                // Search by UserId
                usersPage =
                        userRepo.findByUserId(
                                request.getUserId(),
                                pageable
                        );

            }

            else if (request.getLoginId() != null &&
                    !request.getLoginId().isEmpty()) {

                // Search by LoginId
                usersPage =
                        userRepo.findByLoginIdIgnoreCase(
                                request.getLoginId(),
                                pageable
                        );

            }

            else {

                // Fetch all users
                usersPage =
                        userRepo.findAll(pageable);
            }

        /*
         ===============================
         ENTITY → DTO
         ===============================
         */

            List<UserResponse> responseList =
                    usersPage.getContent()
                            .stream()
                            .map(helperUtill::mapToUserResponse)
                            .collect(Collectors.toList());

        /*
         ===============================
         RESPONSE
         ===============================
         */

            return ResponseUtils.createPageableSuccessResponse(
                    responseList,
                    usersPage.getTotalElements(),
                    usersPage.getTotalPages(),
                    pageable.getPageNumber() + 1,
                    pageable.getPageSize()
            );

        }

        catch (SDDException e) {
            throw e;
        }

        catch (Exception e) {

            logger.error("Error while fetching users", e);

            throw new SDDException(
                    false,
                    "Unexpected error while fetching users"
            );
        }
    }


    @Override
    public ApiResponseWithPageable<List<UserResponse>> getAllInActiveUsers(Long userId,UserRequest request) {
        logger.info("Fetching INACTIVE users. Page: {}, Limit: {}",
                request.getPage(), request.getLimit());
        try {
            AppUser userObj= validationUtilClass.getUserDetails(userId);
            Pageable pageable = helperUtill.getPageable(
                    request.getPage(),
                    request.getLimit()
            );
            Page<AppUser> usersPage = userRepo.findByHospital_HospitalIdAndIsActive(userObj.getHospital().getHospitalId(),0, pageable);
            List<UserResponse> responseList = usersPage.getContent()
                    .stream()
                    .map(helperUtill::mapToUserResponse)
                    .collect(Collectors.toList());
            return ResponseUtils.createPageableSuccessResponse(
                    responseList,
                    usersPage.getTotalElements(),
                    usersPage.getTotalPages(),
                    pageable.getPageNumber() + 1,
                    pageable.getPageSize()
            );
        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching inactive users", e);
            throw new SDDException(false, "Unexpected error while fetching inactive users");
        }
    }



    @Override
    public ApiResponseWithPageable<List<UserResponse>> getUsersByOrg(Long userId,GetUserOrgRequest request) {

        logger.info("Fetching all users by organization type and organization id. Page: {}, Limit: {}",
                request.getPage(), request.getLimit());
        try {
            // Pageable
            Pageable pageable = helperUtill.getPageable(
                    request.getPage(),
                    request.getLimit()
            );

            Page<AppUser> usersPage;

            if(request.getOrgType().equalsIgnoreCase("HOSPITAL")){
                usersPage = userRepo.findByHospital_HospitalId(request.getOrgId(),pageable);
            } else if (request.getOrgType().equalsIgnoreCase("TPA")) {
                usersPage = userRepo.findByTpa_TpaId(request.getOrgId(),pageable);
            } else if (request.getOrgType().equalsIgnoreCase("INSURANCE")) {
                usersPage = userRepo.findByInsurance_InsuranceId(request.getOrgId(),pageable);
            } else if (request.getOrgType().equalsIgnoreCase("DISTRICT")) {
                usersPage = userRepo.findByDistrict_DistrictCode(request.getOrgId(),pageable);
            } else if (request.getOrgType().equalsIgnoreCase("RSHAA")) {
                usersPage = userRepo.findByGovOrg_govOrgId(request.getOrgId(),pageable);
            }else {
                throw new SDDException(false, "User must belong to exactly one organization");
            }

            // Entity → DTO
            List<UserResponse> responseList = usersPage.getContent().stream().map(helperUtill::mapToUserResponse).collect(Collectors.toList());
            return ResponseUtils.createPageableSuccessResponse(
                    responseList,
                    usersPage.getTotalElements(),
                    usersPage.getTotalPages(),
                    pageable.getPageNumber() + 1,   // page starts from 1
                    pageable.getPageSize()
            );
        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching all users by Organization type and Id", e);
            throw new SDDException(false, "Unexpected error while fetching users Organization type and Id");
        }
    }


    @Override
    public ApiResponse<?> getOrgTypes(AllOrgRequest request) {
        try {
            if (request == null || request.getOrgType()==null) {
                throw new SDDException(false, "User Must Belong To Any One Organization");
            }
            // Initialize a generic list
            List<?> data;

            if (request.getOrgType().equalsIgnoreCase("Hospital")) {
                data = hospitalMasterRepository.findAllByIsActive(1L);

            } else if (request.getOrgType().equalsIgnoreCase("TPA")) {
                data = tpaRepository.findAllByIsActive(1L);

            } else if (request.getOrgType().equalsIgnoreCase("District")) {
                data = districtMasterRepository.findByState_StateCodeAndIsActive(8, 1);

            } else if (request.getOrgType().equalsIgnoreCase("RSHAA")) {
                data = govOrgRepository.findAllByIsActive(1L);

            } else if (request.getOrgType().equalsIgnoreCase("INSURANCE")) {
                data = insuranceRepository.findAllByIsActive(1L);

            } else {
                throw new SDDException(false, "Invalid Organization Type");
            }
            return ResponseUtils.createSuccessResponse(data, new TypeReference<List<?>>() {});

        } catch (SDDException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error while fetching Organization List for type: ", ex);
            throw new SDDException(false, "Error while fetching Organization List", ex);
        }
    }



    @Override
    public ApiResponse<UserResponse> getUsersById(UserRequest request) {
        logger.info("Fetching user details. userId: {}, loginId: {}", request.getUserId(), request.getLoginId());
        try {
            AppUser appUser;
            if (request.getUserId() != null) {
                appUser = userRepo.findById(request.getUserId())
                        .orElseThrow(() -> new SDDException(false, ErrorCode.AU1011));
            } else if (request.getLoginId() != null && !request.getLoginId().isEmpty()) {
                appUser = userRepo.findByLoginIdIgnoreCase(request.getLoginId())
                        .orElseThrow(() -> new SDDException(false, ErrorCode.AU1011));
            } else {
                throw new SDDException(false, ErrorCode.AU1012);
            }
            UserResponse response = helperUtill.mapToUserResponse(appUser);
            return ResponseUtils.createSuccessResponse(
                    response,
                    new TypeReference<UserResponse>() {}
            );
        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching user details", e);
            throw new SDDException(false, "Unexpected error while fetching user");
        }
    }


}
