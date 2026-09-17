package com.university.sms.exception;

/**
 * Thrown when a request is well-formed but violates a business rule
 * (e.g. invalid grade string, malformed semester code).
 * Mapped to HTTP 400 Bad Request by GlobalExceptionHandler.
 */
public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message) {
        super(message);
    }
}
