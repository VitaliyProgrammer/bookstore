package com.example.basicbookstoreprojectnew.exception;

/**
 * Thrown for any failed login. The message is the same whether the email is unknown
 * or the password is wrong, so the API does not reveal which emails are registered.
 */
public class InvalidCredentialsException extends RuntimeException {

    public static final String MESSAGE = "Invalid email or password";

    public InvalidCredentialsException() {
        super(MESSAGE);
    }
}
