package com.zalando.ecommerce.exception;

public class InvalidEmailVerificationTokenException extends Exception {
    public InvalidEmailVerificationTokenException(String message) {
        super(message);
    }
}
