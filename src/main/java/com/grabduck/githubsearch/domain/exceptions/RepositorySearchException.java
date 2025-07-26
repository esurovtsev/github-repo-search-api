package com.grabduck.githubsearch.domain.exceptions;

/**
 * Base exception for all repository search related errors.
 * Kept simple by design for better maintainability.
 * In a production environment, a more detailed hierarchy 
 * of exceptions would be implemented.
 */
public class RepositorySearchException extends RuntimeException {
    
    public RepositorySearchException(String message) {
        super(message);
    }
    
    public RepositorySearchException(String message, Throwable cause) {
        super(message, cause);
    }
}
