package com.vladislav.authservice.exceptions;

import lombok.Getter;

public class UserAuthenticateException extends Exception {

    @Getter
    private final String username;

    public UserAuthenticateException(String username) {
        super(String.format("Failed authenticate user with username: %s", username));
        this.username = username;
    }
}
