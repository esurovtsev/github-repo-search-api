package com.grabduck.githubsearch.client.exceptions;

public class GitHubApiServerException extends GitHubApiException {
    
    public GitHubApiServerException(String message) {
        super(message);
    }
    
    public GitHubApiServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
