package com.vladislav.authservice.exceptions;

public class UserExistException extends RuntimeException {
    public UserExistException() {
        super("User already exist");
    }
}
