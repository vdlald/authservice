package com.vladislav.authservice.utils.jwt;

import com.vladislav.authservice.documents.User;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JwtUtilsImpl implements JwtUtils {

    @Value("${app.jwt.expirationTime}")
    private Integer expirationTime;

    private final SecretKey jwtSecretKey;

    @Override
    public String createUserJwt(User user) {
        final long createdAt = System.currentTimeMillis();
        final long expiredAt = createdAt + expirationTime * 60;

        return Jwts.builder()
                .claim("userId", user.getId())
                .claim("username", user.getUsername())
                .claim("roles", user.getRoles())
                .setIssuedAt(new Date(createdAt))
                .setExpiration(new Date(expiredAt))
                .signWith(jwtSecretKey)
                .compact();
    }
}
