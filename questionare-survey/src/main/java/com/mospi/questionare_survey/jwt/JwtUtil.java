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
    // Generate Access Token (Supports Multiple Roles & Active Role)
    // ------------------------------------
    public String generateAccessToken(UserDetails userDetails, UserInfo userinfo, String activeRole) {

        Instant now = Instant.now();
        Map<String, Object> claims = new HashMap<>();

        List<String> allRoles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        claims.put("roles", allRoles);
        claims.put("activeRole", activeRole != null ? activeRole : (allRoles.isEmpty() ? "" : allRoles.get(0)));
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
        } catch (JwtException | IllegalArgumentException ex) {
            log.error("Invalid JWT: {}", ex.getMessage());
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return getAllClaims(token).getSubject();
    }

    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Claims getAllClaimsForExternal(String token) {
        Jws<Claims> jws = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .requireIssuer(issuer)
                .build()
                .parseClaimsJws(token);

        String algorithm = jws.getHeader().getAlgorithm();
        if (!SignatureAlgorithm.HS512.getValue().equals(algorithm)) {
            throw new SecurityException("Invalid JWT algorithm");
        }

        Claims claims = jws.getBody();
        String tokenType = claims.get("tokenType", String.class);
        if (tokenType == null || !"ACCESS".equals(tokenType)) {
            throw new SecurityException("Invalid token type");
        }

        return claims;
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}