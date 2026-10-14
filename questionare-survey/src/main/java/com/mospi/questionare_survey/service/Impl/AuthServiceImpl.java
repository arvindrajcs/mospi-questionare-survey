package com.mospi.questionare_survey.service.Impl;



import com.fasterxml.jackson.core.type.TypeReference;
import com.mospi.questionare_survey.entity.UserInfo;
import com.mospi.questionare_survey.exception.SDDException;
import com.mospi.questionare_survey.helperUtil.ErrorCode;
import com.mospi.questionare_survey.helperUtil.HelperUtils;
import com.mospi.questionare_survey.helperUtil.ResponseUtils;
import com.mospi.questionare_survey.jwt.JwtUtil;
import com.mospi.questionare_survey.jwt.RefreshTokenService;
import com.mospi.questionare_survey.repository.RefreshTokenRepo;
import com.mospi.questionare_survey.repository.UserInfoRepo;
import com.mospi.questionare_survey.request.AuthRequest;
import com.mospi.questionare_survey.request.TokenRefreshRequest;
import com.mospi.questionare_survey.response.ApiResponse;
import com.mospi.questionare_survey.response.AuthResponse;
import com.mospi.questionare_survey.response.VerifyTokenResponse;
import com.mospi.questionare_survey.service.AuthService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {


    private final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepo refreshTokenRepo;
    private final JwtUtil jwtUtil;
    private final UserInfoRepo userInfoRepo;
    private final UserDetailsService userDetailsService;
    private final HelperUtils helperUtils;

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public ApiResponse<AuthResponse> getLogin(AuthRequest req,
                                              HttpServletResponse response,
                                              HttpServletRequest request) {

        String ip = helperUtils.getClientIP(request);


        try {
            String username = req.getUsername();
            String password = req.getPassword();

                if (!StringUtils.hasText(username)) {
                    throw new SDDException(false, ErrorCode.AU1000);
                }

            UserInfo userinfo = userInfoRepo
                        .findByUsernameIgnoreCaseAndIsActive(username, true);
                Authentication auth = authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(username, password));
                    SecurityContextHolder.getContext().setAuthentication(auth);

            // ================= TOKEN GENERATION =================

            AuthResponse resp = new AuthResponse();

            UserDetails userDetails = new User(username, "", List.of());

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

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public ApiResponse<AuthResponse> getRefresh(TokenRefreshRequest request, HttpServletRequest req) {

        try {

            String ip = helperUtils.getClientIP(req);

            Optional<VerifyTokenResponse> opt =
                    refreshTokenService.verifyAndRotate(request.getRefreshToken());

            if (opt.isEmpty()) {
                throw new SDDException(false, ErrorCode.AU1001); // invalid refresh token
            }

            VerifyTokenResponse tokenData = opt.get();

            AuthResponse resp = new AuthResponse();

            UserInfo userInfo = tokenData.getUser();

            // Common UserDetails (works for both)
            UserDetails userDetails =
                    new org.springframework.security.core.userdetails.User(
                            userInfo.getUsername(),
                            "",
                            List.of()
                    );

            // COMMON ACCESS TOKEN
            String newAccessToken = jwtUtil.generateAccessToken(userDetails, userInfo);
            // COMMON REFRESH TOKEN
            String newRefreshToken = refreshTokenService.createRefreshToken1(userInfo != null ? userInfo.getUserId() : null, request.getDeviceInfo(), ip);

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
        try {
            boolean flag = refreshTokenService.revokeAllForUser(request.getRefreshToken());
            if (!flag) {
                throw new SDDException(false, ErrorCode.AU1005);
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
