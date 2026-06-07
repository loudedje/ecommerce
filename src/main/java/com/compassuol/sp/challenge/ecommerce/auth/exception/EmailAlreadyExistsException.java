package com.compassuol.sp.challenge.ecommerce.auth.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super(String.format("Email '%s' is already registered", email));
    }
}
