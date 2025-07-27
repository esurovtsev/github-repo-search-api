package com.grabduck.githubsearch.domain.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.grabduck.githubsearch.domain.model.SearchRequest;
import com.grabduck.githubsearch.domain.model.SearchResults;
import lombok.RequiredArgsConstructor;

/**
 * Service for searching and retrieving GitHub repositories
 */
@Service
@RequiredArgsConstructor
public class RepositoryService {
    
    private final RepositoryProvider repositoryProvider;
    
    /**
     * Search for repositories based on the provided criteria.
     * Results are cached to minimize GitHub API calls.
     * 
     * @param request The search criteria
     * @return Search results containing scored repositories
     * @throws RepositorySearchException if the search fails
     */
    @Cacheable("repositories")
    public SearchResults searchRepositories(SearchRequest request) {
        return repositoryProvider.findRepositories(request);
    }
}
