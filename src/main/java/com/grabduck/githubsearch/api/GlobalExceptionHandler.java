package com.grabduck.githubsearch.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.grabduck.githubsearch.domain.exceptions.RepositorySearchException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Handles validation errors from @Validated parameters from controller.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        log.warn("Validation error: {}", ex.getMessage());
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation error: " + ex.getMessage());
        problemDetail.setTitle("Validation Error");
        
        return problemDetail;
    }
    
    /**
     * Handles errors when request parameters cannot be parsed to the correct type.
     * We might have this if there are any date format issues.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        log.warn("Request parameter type mismatch: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Request parameter type mismatch");
        problemDetail.setTitle("Type Conversion Error");

        return problemDetail;
    }
    

    
    /**
     * Handles repository search exceptions from the domain layer.
     */
    @ExceptionHandler(RepositorySearchException.class)
    public ProblemDetail handleRepositorySearchException(RepositorySearchException ex, HttpServletRequest request) {
        log.error("Repository search failed: {}", ex.getMessage(), ex);
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
        problemDetail.setTitle("Repository Search Failed");
        
        return problemDetail;
    }
    
    /**
     * Handles all other exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleAllExceptions(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception", ex);
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        problemDetail.setTitle("Internal Server Error");
        
        return problemDetail;
    }
}
