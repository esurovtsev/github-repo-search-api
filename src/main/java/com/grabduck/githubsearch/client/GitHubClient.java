package com.grabduck.githubsearch.client;

import java.time.LocalDate;

/**
 * Client interface for interacting with GitHub API to search repositories.
 */
public interface GitHubClient {
    
    /**
     * Search for GitHub repositories.
     * 
     * @param createdSince Optional filter for repositories created on or after this date
     * @param language Optional filter for repository language
     * @param sort How to sort the results ("stars", "forks", or "updated")
     * @param direction Direction to sort ("asc" or "desc")
     * @param page Page number for pagination (zero based)
     * @param size Number of results per page
     * @return GitHub search response containing repositories that match criteria
     */
    GitHubSearchResponseDto searchRepositories(
        LocalDate createdSince, 
        String language,
        String sort,
        String direction,
        int page,
        int size
    );
}
