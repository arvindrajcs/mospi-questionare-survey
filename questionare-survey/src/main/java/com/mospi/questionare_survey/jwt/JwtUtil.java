package com.mospi.questionare_survey.jwt;

import com.rshaa.Auth.entity.AppUser;
import com.rshaa.Auth.entity.RoleMaster;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-expiration-ms}")
    private long accessExpirationMs;

    @Value("${jwt.issuer}")
    private String issuer;


    // ------------------------------------
    // Generate Access Token COMMON FOR BOTH DB and GUEST
    // ------------------------------------
    public String generateAccessToken1(UserDetails userDetails, AppUser appUser) {

        boolean isCitizen = (appUser == null);

        long orgId = 0L;
        String orgName = "Citizen Of Rajasthan";
        String orgType = "Citizen";
        Long isGov = 0L;

        Map<String, Object> claims = new HashMap<>();

        if (!isCitizen) {

            if (appUser.getRole() == null) {
                throw new IllegalStateException("User role missing");
            }

            if (appUser.getHospital() != null) {
                orgId = appUser.getHospital().getHospitalId();
                orgName = appUser.getHospital().getHospitalName();
                orgType = "HOSPITAL";
                isGov = appUser.getHospital().getIsGovernment();

            } else if (appUser.getTpa() != null) {
                orgType = "TPA";
                orgId = appUser.getTpa().getTpaId();
                orgName = appUser.getTpa().getDescription();

            } else if (appUser.getInsurance() != null) {
                orgType = "INSURANCE";
                orgId = appUser.getInsurance().getInsuranceId();
                orgName = appUser.getInsurance().getDescription();

            } else if (appUser.getDistrict() != null) {
                orgType = "DISTRICT";
                orgId = appUser.getDistrict().getDistrictCode();
                orgName = appUser.getDistrict().getDescriptionEnglish();

            } else if (appUser.getGovOrg() != null) {
                orgType = "RSHAA";
                orgId = appUser.getGovOrg().getGovOrgId();
                orgName = appUser.getGovOrg().getDescription();
            }

            claims.put("roles",
                    userDetails.getAuthorities()
                            .stream()
                            .map(GrantedAuthority::getAuthority)
                            .toList());

            claims.put("roleId", appUser.getRole().getRoleId());
            claims.put("roleName", appUser.getRole().getDescription());
            claims.put("userId", appUser.getUserId());
            claims.put("LoginSsoId", appUser.getLoginId());

        } else {
            claims.put("roles", List.of("ROLE_Rajasthan-Citizen"));
            claims.put("roleId", 17);
            claims.put("roleName", "Rajasthan-Citizen");
            claims.put("userId", 0L);
            claims.put("LoginSsoId", userDetails.getUsername());
        }

        claims.put("orgId", orgId);
        claims.put("orgName", orgName);
        claims.put("orgType", orgType);
        claims.put("isGov", isGov);
        claims.put("userType", isCitizen ? "CITIZEN" : "SYSTEM_USER");
        claims.put("tokenType", "ACCESS");

        Instant now = Instant.now();

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuer(issuer)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(accessExpirationMs)))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }


    // ------------------------------------
    // Generate Access Token
    // ------------------------------------
    public String generateAccessToken(UserDetails userDetails, AppUser appUser) {

        long orgId = 0L;
        String orgName = "";
        String orgType = "";
        Long isGov = 0L;

        if (appUser.getHospital() != null
                && appUser.getHospital().getHospitalId() != null) {

            orgId = appUser.getHospital().getHospitalId();
            orgName = appUser.getHospital().getHospitalName();
            orgType = "HOSPITAL";
            isGov = appUser.getHospital().getIsGovernment();

        } else if (appUser.getTpa() != null
                && appUser.getTpa().getTpaId() != null) {

            orgId = appUser.getTpa().getTpaId();
            orgName = appUser.getTpa().getDescription();
            orgType = "TPA";

        } else if (appUser.getInsurance() != null
                && appUser.getInsurance().getInsuranceId() != null) {

            orgId = appUser.getInsurance().getInsuranceId();
            orgName = appUser.getInsurance().getDescription();
            orgType = "INSURANCE";

        } else if (appUser.getDistrict() != null
                && appUser.getDistrict().getDistrictCode() != null) {

            orgId = appUser.getDistrict().getDistrictCode();
            orgName = appUser.getDistrict().getDescriptionEnglish();
            orgType = "DISTRICT";
        }
        else if (appUser.getGovOrg() != null
                && appUser.getGovOrg().getGovOrgId() != null) {

            orgId = appUser.getGovOrg().getGovOrgId();
            orgName = appUser.getGovOrg().getDescription();
            orgType = "RSHAA";
        }else {
//            throw new SDDException(false, "User must belong to exactly one organization");
            orgId = 0L;
            orgName = "Citizen Of Rajasthan";
            orgType = "Citizen";
        }


        Instant now = Instant.now();
        Map<String, Object> claims = new HashMap<>();
        claims.put(
                "roles",
                userDetails.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList())
        );
        // ADD YOUR ROLE NAME & ROLE ID
        claims.put("orgId", orgId);
        claims.put("orgName", orgName);
        claims.put("orgType", orgType);
        claims.put("isGov", isGov);
        claims.put("roleId", appUser.getRole().getRoleId());
        claims.put("roleName", appUser.getRole().getDescription());
        claims.put("userId", appUser.getUserId());
        claims.put("LoginSsoId", appUser.getLoginId());
        claims.put("tokenType", "ACCESS");

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setClaims(claims)
                .setSubject(userDetails.getUsername())       // loginId comes from AppUserPrincipal
                .setIssuer(issuer)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(accessExpirationMs)))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }



    // ------------------------------------
    // Generate Access Token For Citizen
    // ------------------------------------
    public String generateAccessTokenForCitizen(String ssoId) {

        long orgId = 0L;
        String orgName = "Citizen Of Rajasthan";
        String orgType = "Citizen";


        Instant now = Instant.now();
        Map<String, Object> claims = new HashMap<>();
        claims.put(
                "roles",
                List.of("ROLE_Rajasthan-Citizen")
        );
        // ADD YOUR ROLE NAME & ROLE ID
        claims.put("orgId", orgId);
        claims.put("orgName", orgName);
        claims.put("orgType", orgType);
        claims.put("roleId", 17);
        claims.put("roleName", "Rajasthan-Citizen");
        claims.put("userId", 0L);
        claims.put("LoginSsoId", ssoId);
        claims.put("tokenType", "ACCESS");

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setClaims(claims)
                .setSubject(ssoId)
                .setIssuer(issuer)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(accessExpirationMs)))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateAccessTokenForGuestRole(UserDetails userDetails, RoleMaster guestRole) {

        long orgId = 0L;
        String orgName = "Citizen Of Rajasthan";
        String orgType = "Citizen";

        String roleName = "Rajasthan-Citizen";
        Long roleId = 17L;

        if (guestRole != null) {
            if (StringUtils.hasText(guestRole.getDescription())) {
                roleName = guestRole.getDescription();
            }
            if (guestRole.getRoleId() != null) {
                roleId = guestRole.getRoleId();
            }
        }

        Instant now = Instant.now();
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", List.of("ROLE_" + roleName));
        claims.put("orgId", orgId);
        claims.put("orgName", orgName);
        claims.put("orgType", orgType);
        claims.put("roleId", roleId);
        claims.put("roleName", roleName);
        claims.put("userId", 0L);
        claims.put("LoginSsoId", userDetails.getUsername());
        claims.put("userType", "CITIZEN");
        claims.put("tokenType", "ACCESS");

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuer(issuer)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(accessExpirationMs)))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }


    // =========================================================
    // IMPROVEMENT 1: Validate token WITH UserDetails
    // =========================================================

    // =========================================================
// Validate token WITH username (for SSO / Citizen)
// =========================================================
    public boolean validateToken1(String token, String username) {

        try {
            Claims claims = getAllClaims(token);

            String extractedUsername = claims.getSubject();
            Date expiration = claims.getExpiration();

            boolean isUsernameMatch = extractedUsername.equals(username);
            boolean isNotExpired = expiration.after(new Date());

            return isUsernameMatch && isNotExpired;

        } catch (ExpiredJwtException ex) {
            log.warn("JWT expired for user: {}", ex.getClaims().getSubject());
            return false;

        } catch (JwtException | IllegalArgumentException ex) {
            log.error("Invalid JWT: {}", ex.getMessage());
            return false;
        }
    }

    public boolean validateToken(String token, UserDetails userDetails) {

        try {
            Claims claims = getAllClaims(token);

            String username = claims.getSubject();
            Date expiration = claims.getExpiration();

            boolean isUsernameMatch = username.equals(userDetails.getUsername());
            boolean isNotExpired = expiration.after(new Date());

            return isUsernameMatch && isNotExpired;

        } catch (ExpiredJwtException ex) {
            log.warn("JWT expired for user: {}", ex.getClaims().getSubject());
            return false;

        } catch (JwtException | IllegalArgumentException ex) {
            log.error("Invalid JWT: {}", ex.getMessage());
            return false;
        }
    }

    // =========================================================
    // Extract Username
    // =========================================================
    public String getUsernameFromToken(String token) {
        return getAllClaims(token).getSubject();
    }

    // =========================================================
    // Extract Claims (used internally)
    // =========================================================
    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ==========================
    // Extract Claims Securely
    // ==========================

    public Claims getAllClaimsForExternal(String token) {

        Jws<Claims> jws = Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .requireIssuer(issuer)   // IMPORTANT
                .build()
                .parseClaimsJws(token);

        // Validate Algorithm
        String algorithm =
                jws.getHeader().getAlgorithm();

        if (!SignatureAlgorithm.HS512
                .getValue()
                .equals(algorithm)) {

            throw new SecurityException(
                    "Invalid JWT algorithm");
        }

        Claims claims =
                jws.getBody();

        // Validate Token Type
        String tokenType =
                claims.get("tokenType", String.class);

        if (tokenType == null ||
                !"ACCESS".equals(tokenType)) {

            throw new SecurityException(
                    "Invalid token type");
        }

        return claims;
    }

    // ------------------------------------
    // Signing Key
    // ------------------------------------
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }


}
