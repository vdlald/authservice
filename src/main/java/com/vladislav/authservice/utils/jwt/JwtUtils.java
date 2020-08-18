package com.vladislav.authservice.utils.jwt;

import com.vladislav.authservice.documents.User;

public interface JwtUtils {
    String createUserJwt(User user);
}
