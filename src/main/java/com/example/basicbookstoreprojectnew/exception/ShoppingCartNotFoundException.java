package com.example.basicbookstoreprojectnew.exception;

public class ShoppingCartNotFoundException extends RuntimeException {

    public ShoppingCartNotFoundException(String message) {
        super(message);
    }
}
