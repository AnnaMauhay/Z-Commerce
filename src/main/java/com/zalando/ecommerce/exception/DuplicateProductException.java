package com.zalando.ecommerce.exception;

public class DuplicateProductException extends Exception {
    public DuplicateProductException(String message){
        super(message);
    }
    public DuplicateProductException(){
        super();
    }
}
