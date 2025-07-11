package com.grabduck.githubsearch.client;

import com.grabduck.githubsearch.domain.model.SearchRequest;
import com.grabduck.githubsearch.domain.model.SearchResults;
import com.grabduck.githubsearch.domain.service.RepositoryProvider;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

/**
 * Adapter implementation of RepositoryProvider that uses GitHub API.
 */
@Component
@RequiredArgsConstructor
public class GitHubRepositoryAdapter implements RepositoryProvider {
    private final GitHubClient githubClient;

    @Override
    public SearchResults findRepositories(@NonNull SearchRequest request) {
        GitHubSearchResponseDto response = githubClient.searchRepositories(
            request.createdSince(),
            request.language(),
            request.sort().getValue(),
            request.direction().getValue(),
            request.page() - 1,
            request.size()
        );
        
        return new SearchResults(response.total_count(), GitHubSearchResponseDtoMapper.toModel(response.items()));
    }
}
