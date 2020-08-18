package com.vladislav.authservice.exceptions;

import lombok.Getter;

public class UserNotFoundException extends Exception {

    @Getter
    private final String username;

    public UserNotFoundException(String username) {
        super(String.format("User with username: %s not found", username));
        this.username = username;
    }
}
