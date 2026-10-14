package com.mospi.questionare_survey.jwt;

import com.mospi.questionare_survey.entity.UserInfo;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
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
    // Generate Access Token
    // ------------------------------------
    public String generateAccessToken(UserDetails userDetails, UserInfo userinfo) {

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
        claims.put("roleId", "");
        claims.put("roleName", "");
        claims.put("userId", userinfo.getUserId());
        claims.put("username", userinfo.getUsername());
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
