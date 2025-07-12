package com.grabduck.githubsearch.client.exceptions;

public class GitHubApiClientException extends GitHubApiException {
    
    public GitHubApiClientException(String message) {
        super(message);
    }
    
    public GitHubApiClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
