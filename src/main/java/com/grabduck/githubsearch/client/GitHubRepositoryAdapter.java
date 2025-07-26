package com.grabduck.githubsearch.client;

import com.grabduck.githubsearch.domain.exceptions.RepositorySearchException;
import com.grabduck.githubsearch.domain.model.SearchRequest;
import com.grabduck.githubsearch.domain.model.SearchResults;
import com.grabduck.githubsearch.domain.service.RepositoryProvider;
import com.grabduck.githubsearch.client.exceptions.*;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

/**
 * Adapter implementation of RepositoryProvider that uses GitHub API.
 * 
 * Architecture note: we separated here 2 concerns: high-level Adapter and low-level GitHubClient.
 * The idea for a future would be that Adapter takes care of high-level concerns like, caching, maybe retrying 
 * (maybe - because we use Spring WebClient in GitHubClient and that has built-in retrying out of the box already), 
 * hiding low-level technical details such as githib specific exceptions from the domain layer etc, while 
 * low-level GitHubClient is concentrating more on direct communication with github API and handling low-level details.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GitHubRepositoryAdapter implements RepositoryProvider {
    private final GitHubClient githubClient;

    @Override
    public SearchResults findRepositories(@NonNull SearchRequest request) {
        try {
            GitHubSearchResponseDto response = githubClient.searchRepositories(
                request.createdSince(),
                request.language(),
                request.sort().getValue(),
                request.direction().getValue(),
                request.page() - 1,
                request.size()
            );
            
            return new SearchResults(response.total_count(), GitHubSearchResponseDtoMapper.toModel(response.items()));

        } catch (GitHubApiException e) {
            // Translate all low level GitHub API exceptions to a generic domain exception with an appropriate message
            StringBuilder messageBuilder = new StringBuilder("Repository search failed: ");
            
            if (e instanceof GitHubApiRateLimitException) {
                messageBuilder.append("API rate limit exceeded. Please try again later.");
                log.warn("GitHub API rate limit exceeded", e);

            } else if (e instanceof GitHubApiClientException) {
                messageBuilder.append("Invalid search criteria.");
                log.warn("GitHub API client error: {}", e.getMessage(), e);

            } else if (e instanceof GitHubApiServerException) {
                messageBuilder.append("External service temporarily unavailable.");
                log.error("GitHub API server error", e);

            } else {
                messageBuilder.append(e.getMessage());
                log.error("Unexpected GitHub API error", e);
            }
            
            throw new RepositorySearchException(messageBuilder.toString(), e);

        } catch (Exception e) {
            log.error("Unexpected error during repository search", e);
            throw new RepositorySearchException("An unexpected error occurred during repository search", e);
        }
    }
}
