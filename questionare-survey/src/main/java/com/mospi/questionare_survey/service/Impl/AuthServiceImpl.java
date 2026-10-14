package com.mospi.questionare_survey.service.Impl;


import com.fasterxml.jackson.core.type.TypeReference;
import com.mospi.questionare_survey.request.AuthRequest;
import com.mospi.questionare_survey.response.ApiResponse;
import com.mospi.questionare_survey.response.AuthResponse;
import com.mospi.questionare_survey.service.AuthService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {


    @Transactional(rollbackFor = {Exception.class})
    @Override
    public ApiResponse<AuthResponse> getLogin(AuthRequest req,
                                              HttpServletResponse response,
                                              HttpServletRequest request) {

        ValidationUtil.validateLoginRequest(req);

        String ip = helperUtill.getClientIP(request);
        String loginId;
        String moduleType = normalizeModuleType(req.getModuleType());

        if (!ALLOWED_MODULE_TYPES.contains(moduleType)) {
            throw new SDDException(false, "Invalid module type. Allowed values are TMS, SR, HEM, CITIZEN, CRC");
        }

        boolean bypassDbValidation = DB_VALIDATION_BYPASS_MODULE_TYPES.contains(moduleType);
        boolean isHemModule = "HEM".equals(moduleType);
        boolean isCitizenModule = "CITIZEN".equals(moduleType);
        boolean isGuestFallbackModule = isHemModule || isCitizenModule;

        try {

            boolean isSSOLogin = StringUtils.hasText(req.getToken())
                    && !isGuestFallbackModule
                    && (bypassDbValidation || !StringUtils.hasText(req.getUsername()));

            AppUser appUser = null;
            RoleMaster fallbackCitizenRole = null;

            // ================= SSO LOGIN (Citizen + DB user) =================
            if (isSSOLogin) {

                loginId = helperUtill.extractLoginIdFromToken(req.getToken());
                //loginId = req.getUsername();

                if (!jwtUtil.validateToken1(req.getToken(), loginId)) {
                    throw new SDDException(false, ErrorCode.AU1000);
                }

                if (!bypassDbValidation) {
                    appUser = userRepo
                            .findByLoginIdIgnoreCaseAndIsActiveAndRoleIsNotNull(loginId, 1);

                    if (appUser == null) {
                        throw new SDDException(false, ErrorCode.AU1000);
                    }
                }

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(loginId, null, List.of());

                SecurityContextHolder.getContext().setAuthentication(auth);
            }

            // ================= NORMAL LOGIN (ONLY DB USER) =================
            else {

                loginId = req.getUsername();
                String password = req.getPassword();

                if (!StringUtils.hasText(loginId)) {
                    throw new SDDException(false, ErrorCode.AU1000);
                }

                appUser = userRepo
                        .findByLoginIdIgnoreCaseAndIsActiveAndRoleIsNotNull(loginId, 1);

                if (appUser == null) {
                    if (isGuestFallbackModule && userRepo.findByLoginIdIgnoreCase(loginId).isEmpty()) {
                        fallbackCitizenRole = resolveRajasthanCitizenRole();

                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(loginId, null, List.of());
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    } else {
                        throw new SDDException(false, ErrorCode.AU1000);
                    }
                } else {
                    Authentication auth = authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(loginId, password));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }

            }
            // ================= TOKEN GENERATION =================

            AuthResponse resp = new AuthResponse();

            UserDetails userDetails = new User(loginId, "", List.of());

            String accessToken;
            if (fallbackCitizenRole != null) {
                accessToken = jwtUtil.generateAccessTokenForGuestRole(userDetails, fallbackCitizenRole);
            } else {
                accessToken = jwtUtil.generateAccessToken1(userDetails, appUser);
            }

            String refreshToken = refreshTokenService.createRefreshToken1(appUser != null ? appUser.getUserId() : null, loginId, req.getDeviceInfo(), ip);

            resp.setAccessToken(accessToken);
            resp.setRefreshToken(refreshToken);

            return ResponseUtils.createSuccessResponse(
                    resp, new TypeReference<AuthResponse>() {}
            );

        } catch (BadCredentialsException ex) {
            throw new SDDException(false, ErrorCode.AU1000);
        } catch (SDDException e) {
            throw e;
        } catch (Exception ex) {
            log.error("Error during login ", ex);
            throw new SDDException(false, "Authentication failed");
        }
    }

    private String normalizeModuleType(String moduleType) {
        if (!StringUtils.hasText(moduleType)) {
            throw new SDDException(false, "Module Type Can Not be Null eg. TMS/SR/HEM/CITIZEN/CRC");
        }
        return moduleType.trim().toUpperCase();
    }

    private RoleMaster resolveRajasthanCitizenRole() {
        List<String> roleCandidates = List.of(
                "Rajasthan-Citizen",
                "Rajasthan Citizen",
                "Citizen Of Rajasthan"
        );

        for (String roleName : roleCandidates) {
            Optional<RoleMaster> roleOpt = roleMasterRepository.findByDescriptionIgnoreCase(roleName);
            if (roleOpt.isPresent()) {
                return roleOpt.get();
            }
        }

        return roleMasterRepository.findAllByIsActive(1L)
                .stream()
                .filter(Objects::nonNull)
                .filter(role -> StringUtils.hasText(role.getDescription()))
                .filter(role -> {
                    String roleDesc = role.getDescription().toLowerCase(Locale.ROOT);
                    return roleDesc.contains("rajasthan") && roleDesc.contains("citizen");
                })
                .findFirst()
                .orElseThrow(() -> new SDDException(false, "Rajasthan Citizen role not found in MST_ROLE"));
    }


    @Transactional(rollbackFor = {Exception.class})
    @Override
    public ApiResponse<AuthResponse> getRefresh(TokenRefreshRequest request, HttpServletRequest req) {

        ValidationUtil.validateRefreshTokenRequest(request);

        try {

            String ip = helperUtill.getClientIP(req);

            Optional<VerifyTokenResponse> opt =
                    refreshTokenService.verifyAndRotate(request.getRefreshToken());

            if (opt.isEmpty()) {
                throw new SDDException(false, ErrorCode.AU1001); // invalid refresh token
            }

            VerifyTokenResponse tokenData = opt.get();

            AuthResponse resp = new AuthResponse();

            AppUser appUser = tokenData.getUser();
            String loginId;

            if (appUser != null) {
                loginId = appUser.getLoginId();
            } else {
                loginId = tokenData.getSsoId();
            }

            // Common UserDetails (works for both)
            UserDetails userDetails =
                    new org.springframework.security.core.userdetails.User(
                            loginId,
                            "",
                            List.of()
                    );

            // COMMON ACCESS TOKEN
            String newAccessToken = jwtUtil.generateAccessToken1(userDetails, appUser);
            // COMMON REFRESH TOKEN
            String newRefreshToken = refreshTokenService.createRefreshToken1(appUser != null ? appUser.getUserId() : null, loginId, request.getDeviceInfo(), ip);

            resp.setAccessToken(newAccessToken);
            resp.setRefreshToken(newRefreshToken);

            return ResponseUtils.createSuccessResponse(
                    resp, new TypeReference<AuthResponse>() {}
            );

        }
        catch (SDDException e) {
            throw e;
        }
        catch (Exception ex) {
            log.error("Error while refreshing token", ex);
            throw new SDDException(false, "Token refresh failed");
        }
    }



    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<Boolean> getValidateToken(String authHeader) {
        try {
            // Check header
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new SDDException(false, ErrorCode.AU1002); // Invalid token format
            }
            // Extract token
            String token = authHeader.substring(7);
            String username;
            // Extract username from token
            try {
                username = jwtUtil.getUsernameFromToken(token);
            } catch (ExpiredJwtException e) {
                throw new SDDException(false, "JWT token expired");
            } catch (JwtException e) {
                throw new SDDException(false, "Invalid JWT token");
            }
            // Username must exist
            if (username == null) {
                throw new SDDException(false, "Username not found in token");
            }
            // Load user
            UserDetails userDetails =
                    userDetailsService.loadUserByUsername(username);
            // Validate token
            boolean isValid = jwtUtil.validateToken(token, userDetails);
            if (!isValid) {
                throw new SDDException(false, "JWT token validation failed");
            }
            // Success
            return new ApiResponse<>(true, true, "Token is valid");
        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error while validating token", e);
            throw new SDDException(false, "Error while validating token");
        }
    }




    @Transactional(rollbackFor = {Exception.class})
    @Override
    public ApiResponse<String> getLogout(TokenRefreshRequest request) {

        ValidationUtil.validateRefreshTokenRequest(request);

        try {
            boolean flag = refreshTokenService.revoke(request.getRefreshToken());
            if (!flag) {
                throw new SDDException(false, ErrorCode.AU1004);
            }
            String msg = "User Logout Successfully from Current Device";
            log.info("Fetched {} User Current Device from login", request.getDeviceInfo());
            return ResponseUtils.createSuccessResponse(msg, new TypeReference<String>(){});
        }
        catch (SDDException e) {
            throw e;
        }
        catch (Exception ex) {
            log.error("Logout from Current device error", ex);
            throw new SDDException(false, "Unexpected error during logout from Current device");
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public ApiResponse<String> getLogoutAllDevice(TokenRefreshRequest request) {
        ValidationUtil.validateRefreshTokenRequest(request);
        try {
            boolean flag = refreshTokenService.revokeAllForUser(request.getRefreshToken());
            if (!flag) {
                throw new SDDException(false, ErrorCode.AU1004);
            }
            String msg = "User Logout Successfully from All Device";
            log.info("Fetched {} User Device from login", request.getDeviceInfo());
            return ResponseUtils.createSuccessResponse(msg, new TypeReference<String>(){});
        }
        catch (SDDException e) {
            throw e;
        }
        catch (Exception ex) {
            log.error("Logout from All Device error", ex);
            throw new SDDException(false, "Unexpected error during logout from All Device");
        }
    }



}
