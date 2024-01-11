package com.zalando.ecommerce.exception;

public class UserNotFoundException extends Exception {
    public UserNotFoundException(String message) {
        super(message);
    }
    public UserNotFoundException() {
        super();
    }
}
