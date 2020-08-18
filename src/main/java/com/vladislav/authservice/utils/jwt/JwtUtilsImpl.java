package com.vladislav.authservice.utils.jwt;

import com.vladislav.authservice.documents.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtilsImpl implements JwtUtils {

    private static final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @Value("${app.jwt.expirationTime}")
    private Integer expirationTime;

    @Value("${app.jwt.secret-key}")
    private String secret;

    @Override
    public String createUserJwtToken(User user) {
        final long createdAt = System.currentTimeMillis();
        final long expiredAt = createdAt + expirationTime;

        return Jwts.builder()
                .claim("userId", user.getId())
                .claim("username", user.getUsername())
                .claim("roles", user.getRoles())
                .setIssuedAt(new Date(createdAt))
                .setExpiration(new Date(expiredAt))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }
}
