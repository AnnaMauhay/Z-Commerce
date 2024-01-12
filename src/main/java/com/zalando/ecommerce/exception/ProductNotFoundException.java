package com.zalando.ecommerce.exception;

public class ProductNotFoundException extends Exception {
    public ProductNotFoundException(String message){
        super(message);
    }
    public ProductNotFoundException(){
        super();
    }
}
