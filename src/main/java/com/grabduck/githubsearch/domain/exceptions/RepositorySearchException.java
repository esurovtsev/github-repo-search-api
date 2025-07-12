package com.grabduck.githubsearch.domain.exceptions;

/**
 * Base exception for all repository search related errors.
 * We decided to keep it simple for this technical challenge.
 * In a production there should be a a more detailed hierarchy 
 * of exceptions created.
 */
public class RepositorySearchException extends RuntimeException {
    
    public RepositorySearchException(String message) {
        super(message);
    }
    
    public RepositorySearchException(String message, Throwable cause) {
        super(message, cause);
    }
}
