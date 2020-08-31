package com.vladislav.authservice.exceptions;

public class RefreshTokenExpired extends Exception {
    public RefreshTokenExpired() {
        super("Refresh token expired");
    }
}
