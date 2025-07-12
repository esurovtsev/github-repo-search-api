package com.grabduck.githubsearch.client.exceptions;

public class GitHubApiRateLimitException extends GitHubApiException {
    
    public GitHubApiRateLimitException(String message) {
        super(message);
    }
    
    public GitHubApiRateLimitException(String message, Throwable cause) {
        super(message, cause);
    }
}
