package com.vladislav.authservice.services;

import com.vladislav.authservice.documents.User;
import com.vladislav.authservice.exceptions.RefreshTokenExpired;

public interface JwtService {
    String createUserAccessJwt(User user);

    User.RefreshToken createUserRefreshToken();

    void updateRefreshToken(User.RefreshToken refreshToken) throws RefreshTokenExpired;
}
