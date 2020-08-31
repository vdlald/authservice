package com.vladislav.authservice.services;

import com.vladislav.authservice.documents.User;
import com.vladislav.authservice.exceptions.RefreshTokenExpired;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JwtServiceImpl implements JwtService {

    @Value("${app.jwt.access-expiration-time}")
    private Integer accessExpirationTime;

    @Value("${app.jwt.refresh-expiration-time}")
    private Integer refreshExpirationTime;

    private final SecretKey jwtSecretKey;

    @Override
    public String createUserAccessJwt(User user) {
        final long createdAt = System.currentTimeMillis();
        final long expiredAt = createdAt + accessExpirationTime * 60 * 60;

        return Jwts.builder()
                .claim("userId", user.getId())
                .claim("username", user.getUsername())
                .claim("roles", user.getRoles())
                .setIssuedAt(new Date(createdAt))
                .setExpiration(new Date(expiredAt))
                .signWith(jwtSecretKey)
                .compact();
    }

    @Override
    public User.RefreshToken createUserRefreshToken() {
        return new User.RefreshToken();
    }

    @Override
    public void updateRefreshToken(User.RefreshToken refreshToken) throws RefreshTokenExpired {
        final LocalDateTime tokenCreatedAt = refreshToken.getCreatedAt();
        if (tokenCreatedAt.plusMinutes(refreshExpirationTime).isAfter(LocalDateTime.now())) {
            refreshToken.setCreatedAt(LocalDateTime.now());
            refreshToken.setRefreshToken(UUID.randomUUID());
        } else {
            throw new RefreshTokenExpired();
        }
    }
}
