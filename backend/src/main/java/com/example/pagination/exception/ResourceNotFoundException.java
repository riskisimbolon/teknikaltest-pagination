package com.example.pagination.exception;

public class ResourceNotFoundException extends RuntimeException {
    ResourceNotFoundException(String message) {
        super(message);
    }
}
