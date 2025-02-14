package com.assignment.urlShortener.exception;

public class CustomIdAlreadyExistsException extends RuntimeException {
    public CustomIdAlreadyExistsException(String message) {
        super(message);
    }
} 
