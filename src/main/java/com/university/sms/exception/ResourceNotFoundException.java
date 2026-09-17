package com.university.sms.exception;

/**
 * Thrown when a requested entity (Student, Course, Enrollment) does not
 * exist in the database. Mapped to HTTP 404 by GlobalExceptionHandler.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException forEntity(String entityName, Object id) {
        return new ResourceNotFoundException(entityName + " not found with id: " + id);
    }
}
