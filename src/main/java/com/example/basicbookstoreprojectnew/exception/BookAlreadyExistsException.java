package com.example.basicbookstoreprojectnew.exception;

public class BookAlreadyExistsException extends RuntimeException {

    public BookAlreadyExistsException(String message) {
        super(message);
    }
}
