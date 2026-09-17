package com.university.sms.exception;

import com.university.sms.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Centralized exception handling for the entire REST API.
 *
 * Every exception thrown anywhere in the controller/service layers is
 * caught here and converted into a consistent, safe JSON ErrorResponse
 * instead of leaking a stack trace to the client or crashing the request
 * thread. This is the application's core "graceful fallback" mechanism:
 * no single bad request or unexpected null can bring the API down.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ---- Domain-specific, expected exceptions ----------------------------

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req, null);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateResourceException ex, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), req, null);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequest(InvalidRequestException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req, null);
    }

    // ---- Bean validation failures (e.g. @NotBlank, @Email on request body) ----

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .collect(Collectors.toList());
        return build(HttpStatus.BAD_REQUEST, "Validation failed for one or more fields", req, details);
    }

    // ---- Database-level constraint violations (unique keys, FK constraints) ----

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT,
                "The request could not be completed because it violates a database constraint " +
                        "(e.g. a duplicate unique value, or a referenced record still in use).",
                req, null);
    }

    // ---- Malformed input ----

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "Malformed JSON request body.", req, null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        String msg = String.format("Parameter '%s' should be of type %s.", ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "a different type");
        return build(HttpStatus.BAD_REQUEST, msg, req, null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
        return build(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage(), req, null);
    }

    // ---- Catch-all fallback: guarantees the API NEVER returns an
    //      unhandled 500 stack trace to the client. This is the last line
    //      of defense so the application "remains functional under
    //      various conditions" as required by the assignment brief. ----

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest req) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please try again or contact the administrator.",
                req, null);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, HttpServletRequest req, List<String> details) {
        ErrorResponse body = new ErrorResponse(status.value(), status.getReasonPhrase(), message, req.getRequestURI());
        body.setDetails(details);
        return ResponseEntity.status(status).body(body);
    }
}
