package com.zalando.ecommerce.exception;

public class InsufficientStockException extends Exception {
    public InsufficientStockException(String message) {
        super(message);
    }

    public InsufficientStockException() {
        super();
    }
}
