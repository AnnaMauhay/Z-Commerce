package com.zalando.ecommerce.exception;

public class StockLimitExceededException extends Exception {
    public StockLimitExceededException(String message) {
        super(message);
    }
    public StockLimitExceededException() {
        super();
    }
}
