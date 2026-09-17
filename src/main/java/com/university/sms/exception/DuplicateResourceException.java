package com.university.sms.exception;

/**
 * Thrown when an operation would violate a uniqueness rule
 * (e.g. duplicate registration number, email, course code, or a student
 * being enrolled in the same course/semester twice).
 * Mapped to HTTP 409 Conflict by GlobalExceptionHandler.
 */
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
