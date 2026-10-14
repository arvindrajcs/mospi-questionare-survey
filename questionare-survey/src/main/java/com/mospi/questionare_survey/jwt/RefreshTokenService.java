package com.mospi.questionare_survey.jwt;

import com.mospi.questionare_survey.entity.RefreshToken;
import com.mospi.questionare_survey.entity.UserInfo;
import com.mospi.questionare_survey.exception.SDDException;
import com.mospi.questionare_survey.helperUtil.ErrorCode;
import com.mospi.questionare_survey.repository.RefreshTokenRepo;
import com.mospi.questionare_survey.repository.UserInfoRepo;
import com.mospi.questionare_survey.response.VerifyTokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private final RefreshTokenRepo refreshTokenRepo;
    private final UserInfoRepo userInfoRepo;

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    // ---------------------------------------------------------
    // CREATE REFRESH TOKEN COMMON FOR BOTH DB and GUEST
    // ---------------------------------------------------------

    public String createRefreshToken1(Long userId,
                                     String username,
                                     String deviceInfo,
                                     String ip) {

        String token = generateSecureToken();
        String hash = hash(token);

        RefreshToken rt = new RefreshToken();

        rt.setUserId(userId != null ? userId : 0L);
        rt.setUsername(username);
        rt.setIpAddress(ip);
        rt.setTokenHash(hash);
        rt.setCreatedAt(Instant.now());
        rt.setExpiresAt(Instant.now().plusMillis(refreshExpirationMs));
        rt.setDeviceInfo(deviceInfo);
        rt.setRevoked(false);

        refreshTokenRepo.save(rt);

        return token;
    }


    // ---------------------------------------------------------
    // CREATE REFRESH TOKEN
    // ---------------------------------------------------------
    public String createRefreshToken(Long userId, String deviceInfo,String ip) {
        String token = generateSecureToken();
        String hash = hash(token);
        RefreshToken rt = new RefreshToken();
        rt.setUserId(userId);
        rt.setIpAddress(ip);
        rt.setTokenHash(hash);
        rt.setCreatedAt(Instant.now());
        rt.setExpiresAt(Instant.now().plusMillis(refreshExpirationMs));
        rt.setDeviceInfo(deviceInfo);
        rt.setRevoked(false);
        refreshTokenRepo.save(rt);
        return token; // return raw token to client
    }



    // ---------------------------------------------------------
    // CREATE REFRESH TOKEN FOR CITIZEN
    // ---------------------------------------------------------
    public String createRefreshTokenForCitizen(String username, String deviceInfo,String ip) {
        String token = generateSecureToken();
        String hash = hash(token);
        RefreshToken rt = new RefreshToken();
        rt.setUserId(0L);
        rt.setUsername(username);
        rt.setIpAddress(ip);
        rt.setTokenHash(hash);
        rt.setCreatedAt(Instant.now());
        rt.setExpiresAt(Instant.now().plusMillis(refreshExpirationMs));
        rt.setDeviceInfo(deviceInfo);
        rt.setRevoked(false);
        refreshTokenRepo.save(rt);
        return token; // return raw token to client
    }


    // ---------------------------------------------------------
    // VERIFY + ROTATE REFRESH TOKEN
    // ---------------------------------------------------------
//    public Optional<AppUser> verifyAndRotate(String presentedToken) {
//        String hashValue = hash(presentedToken);
//        Optional<RefreshToken> tokenOpt = refreshTokenRepository.findByTokenHash(hashValue);
//        if (tokenOpt.isEmpty()) return Optional.empty();
//        RefreshToken token = tokenOpt.get();
//        // expired or revoked
//        if (token.getExpiresAt().isBefore(Instant.now()) || token.isRevoked()) {
//            token.setRevoked(true);
//            refreshTokenRepository.save(token);
//            return Optional.empty();
//        }
//        // ROTATE token (revoke old one)
//        token.setRevoked(true);
//        refreshTokenRepository.save(token);
//        // return user by userId
//        Optional<AppUser> user = userRepository.findByUserId(token.getUserId());
//        return user;
//    }


    // ---------------------------------------------------------
// VERIFY + ROTATE REFRESH TOKEN FOR CITIZEN INCLUDE ALSO
// ---------------------------------------------------------
    public Optional<VerifyTokenResponse> verifyAndRotate(String presentedToken) {

        String hashValue = hash(presentedToken);

        Optional<RefreshToken> tokenOpt =
                refreshTokenRepo.findByTokenHash(hashValue);

        if (tokenOpt.isEmpty()) {
            return Optional.empty();
        }

        RefreshToken token = tokenOpt.get();

        // Expired or revoked
        if (token.getExpiresAt().isBefore(Instant.now()) || token.isRevoked()) {
            token.setRevoked(true);
            refreshTokenRepo.save(token);
            return Optional.empty();
        }

        // Rotate (revoke old token)
        token.setRevoked(true);
        refreshTokenRepo.save(token);

        // Fetch user
        UserInfo user =
                userInfoRepo.findByUsernameIgnoreCaseAndIsActive(token.getUsername(), true);

        if (user != null) {
            return Optional.of(new VerifyTokenResponse(user));
        } else {
            return Optional.of(new VerifyTokenResponse(null));
        }
    }

    // ---------------------------------------------------------
    // REVOKE SINGLE TOKEN (used in logout)
    // ---------------------------------------------------------
    public boolean revoke(String refreshToken) {
        String hashValue = hash(refreshToken);
        Optional<RefreshToken> tokenOpt = refreshTokenRepo.findByTokenHash(hashValue);
        if (tokenOpt.isPresent()) {
            RefreshToken token = tokenOpt.get();
            token.setRevoked(true);
            refreshTokenRepo.save(token);
            return true;
        }else{
            return false;
        }
    }

    // ---------------------------------------------------------
    // REVOKE ALL TOKENS FOR A USER (optional)
    // ---------------------------------------------------------
    public boolean revokeAllForUser(String refreshToken) {
        String hashValue = hash(refreshToken);
        Optional<RefreshToken> tokenOpt = refreshTokenRepo.findByTokenHash(hashValue);
        if (tokenOpt.isEmpty()) {
            return false;
        }
        Long userId = tokenOpt.get().getUserId();
        List<RefreshToken> tokens = refreshTokenRepo.findByUserIdAndRevokedFalse(userId);
        if (tokens.isEmpty()) {
            return false;
        }
        tokens.forEach(t -> t.setRevoked(true));
        refreshTokenRepo.saveAll(tokens);
        return true;
    }

    // ---------------------------------------------------------
    // GET USER ID FROM REFRESH TOKENS FOR A USER (optional)
    // ---------------------------------------------------------
    public Long getUserIdFromRefreshToken(String refreshToken) {
        String hash = hash(refreshToken);

        RefreshToken rt = refreshTokenRepo.findByTokenHash(hash)
                .orElseThrow(() -> {
                    log.warn("Invalid or expired refresh token provided");
                    return new SDDException(false, ErrorCode.AU1006);
                });

        return rt.getUserId();
    }


    // ---------------------------------------------------------
    // HELPERS
    // ---------------------------------------------------------
    private String generateSecureToken() {
        byte[] random = new byte[64];
        new SecureRandom().nextBytes(random);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(random);
    }

    private String hash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing failed", e);
        }
    }


}
