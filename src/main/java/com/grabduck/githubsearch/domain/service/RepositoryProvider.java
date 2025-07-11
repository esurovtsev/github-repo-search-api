package com.grabduck.githubsearch.domain.service;

import com.grabduck.githubsearch.domain.model.SearchRequest;
import com.grabduck.githubsearch.domain.model.SearchResults;

/**
 * An interface for repository data providers.
 * 
 * Following hexagonal architecture principles, this interface belongs to the domain layer,
 * while implementations will be in the infrastructure/adapter layer.
 */
public interface RepositoryProvider {
    
    /**
     * Find repositories matching the given criteria.
     * 
     * @param criteria Search criteria containing all filtering, sorting and pagination parameters
     * @return SearchResults containing matching repositories and total count
     */
    SearchResults findRepositories(SearchRequest criteria);
}
