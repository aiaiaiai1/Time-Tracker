package org.project.timetracker.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public final class TokenProcessor {

    private static final String CLAIM_KEY_USER_ID = "userId";
    private static final String ACCESS_TOKEN = "accessToken";
    private final SecretKey secretKey;
    private final Long accessExpiredTime;

    public TokenProcessor(
            @Value("${jwt.token.secret-key}") String secretKey,
            @Value("${jwt.token.access-expiration-time}") Long accessExpiredTime
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessExpiredTime = accessExpiredTime;
    }

    private String generateToken(Long userId, Long expiredTime) {
        long now = System.currentTimeMillis();
        String jwt = Jwts.builder()
                .header()
                .add("alg", "HS256")
                .type("jwt").and()
                .claims()
                .add(CLAIM_KEY_USER_ID, String.valueOf(userId))
                .subject(ACCESS_TOKEN)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expiredTime)).and()
                .signWith(secretKey)
                .compact();
        return jwt;
    }

    public String generateAccessToken(Long userId) {
        return generateToken(userId, accessExpiredTime);
    }

    private Long parseToken(String token) {
        Claims payload = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        String userId = payload
                .get(CLAIM_KEY_USER_ID, String.class);
        return Long.valueOf(userId);
    }

}


