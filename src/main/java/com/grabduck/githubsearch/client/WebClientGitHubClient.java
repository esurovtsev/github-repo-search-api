package com.grabduck.githubsearch.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.grabduck.githubsearch.client.exceptions.GitHubApiClientException;
import com.grabduck.githubsearch.client.exceptions.GitHubApiRateLimitException;
import com.grabduck.githubsearch.client.exceptions.GitHubApiServerException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.util.StringUtils;

/**
 * Implementation of GitHubClient using WebClient for making API calls to GitHub.
 */
@Component
public class WebClientGitHubClient implements GitHubClient {
    
    private final WebClient webClient;
    
    public WebClientGitHubClient(
            WebClient.Builder webClientBuilder, 
            @Value("${github.api.baseUrl}") String baseUrl,
            @Value("${github.api.token}") String token,
            @Value("${github.api.version}") String apiVersion
    ) {
        webClient = webClientBuilder
                .baseUrl(baseUrl)                
                .defaultHeader("Accept", "application/vnd.github+json")
                .defaultHeader("Authorization", "Bearer " + token)
                .defaultHeader("X-GitHub-Api-Version", apiVersion)
                .build();
    }
    
    @Override
    public GitHubSearchResponseDto searchRepositories(
            LocalDate createdSince,
            String language,
            String sort,
            String direction,
            int page,
            int size
    ) {        
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search/repositories")
                        .queryParamIfPresent("q", buildQueryString(createdSince, language))

                        .queryParamIfPresent("sort", Optional.ofNullable(sort))
                        .queryParamIfPresent("order", Optional.ofNullable(direction))

                        .queryParam("page", page + 1)
                        .queryParam("per_page", size)
                        .build())
                .retrieve()
                .onStatus(
                    // we do not have a real limitation for rate limiting here.
                    // for that we would need to parse response headers and see if we met the limit.
                    // instead here for simplicity we just pretend that every 403 response means we hit the limit.
                    status -> status.value() == 403,
                    response -> response.bodyToMono(String.class).map(GitHubApiRateLimitException::new)
                )
                .onStatus(
                    status -> status.is4xxClientError(),
                    response -> response.bodyToMono(String.class).map(GitHubApiClientException::new)
                )
                .onStatus(
                    status -> status.is5xxServerError(),
                    response -> response.bodyToMono(String.class).map(GitHubApiServerException::new)
                )
                .bodyToMono(GitHubSearchResponseDto.class)
                .block();
    }
    
    private Optional<String> buildQueryString(LocalDate createdSince, String language) {
        List<String> queryParts = new ArrayList<>();
        
        if (createdSince != null) {
            queryParts.add("created:>=" + createdSince);
        }
        
        if (StringUtils.hasText(language)) {
            queryParts.add("language:" + language);
        }

        return queryParts.isEmpty() ? Optional.empty() : Optional.of(String.join("+", queryParts));
    }
}
