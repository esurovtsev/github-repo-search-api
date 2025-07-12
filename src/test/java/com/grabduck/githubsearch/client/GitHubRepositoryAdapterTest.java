package com.grabduck.githubsearch.client;

import com.grabduck.githubsearch.client.exceptions.GitHubApiClientException;
import com.grabduck.githubsearch.client.exceptions.GitHubApiException;
import com.grabduck.githubsearch.client.exceptions.GitHubApiRateLimitException;
import com.grabduck.githubsearch.client.exceptions.GitHubApiServerException;
import com.grabduck.githubsearch.domain.exceptions.RepositorySearchException;
import com.grabduck.githubsearch.domain.model.SearchRequest;
import com.grabduck.githubsearch.domain.model.SearchResults;
import com.grabduck.githubsearch.domain.model.SortDirection;
import com.grabduck.githubsearch.domain.model.SortField;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GitHubRepositoryAdapterTest {

    @Mock
    private GitHubClient githubClient;

    @InjectMocks
    private GitHubRepositoryAdapter underTest;

    @Test
    void findRepositories_shouldMapSuccessfulResponse() {
        SearchRequest request = new SearchRequest(
            "Java",
            LocalDate.of(2020, 1, 1),
            SortField.STARS,
            SortDirection.DESC,
            1,
            10
        );

        GitHubRepositoryDto repo1 = new GitHubRepositoryDto(
            1L, "repo1", "owner/repo1", "url1", "desc1",
            "Java", 100, 50, LocalDateTime.now(), LocalDateTime.now()
        );
        
        GitHubRepositoryDto repo2 = new GitHubRepositoryDto(
            2L, "repo2", "owner/repo2", "url2", "desc2",
            "Java", 200, 100, LocalDateTime.now(), LocalDateTime.now()
        );

        GitHubSearchResponseDto response = new GitHubSearchResponseDto(
            2,
            false,
            List.of(repo1, repo2)
        );

        when(githubClient.searchRepositories(
            eq(LocalDate.of(2020, 1, 1)),
            eq("Java"),
            eq("stars"),
            eq("desc"),
            eq(0),
            eq(10)
        )).thenReturn(response);

        SearchResults results = underTest.findRepositories(request);

        // check some basic props only
        assertThat(results).isNotNull();
        assertThat(results.total()).isEqualTo(2);
        assertThat(results.items()).hasSize(2);
        assertThat(results.items().get(0).id()).isEqualTo(1L);
        assertThat(results.items().get(1).id()).isEqualTo(2L);
    }

    @Test
    void findRepositories_shouldTranslateRateLimitException() {
        SearchRequest request = new SearchRequest(
            "Java",
            LocalDate.of(2020, 1, 1),
            SortField.STARS,
            SortDirection.DESC,
            1,
            10
        );

        when(githubClient.searchRepositories(any(), any(), any(), any(), anyInt(), anyInt()))
            .thenThrow(new GitHubApiRateLimitException("API rate limit exceeded"));

        assertThatThrownBy(() -> underTest.findRepositories(request))
            .isInstanceOf(RepositorySearchException.class)
            .hasCauseInstanceOf(GitHubApiRateLimitException.class);
    }

    @Test
    void findRepositories_shouldTranslateClientException() {
        SearchRequest request = new SearchRequest(
            "Java",
            LocalDate.of(2020, 1, 1),
            SortField.STARS,
            SortDirection.DESC,
            1,
            10
        );

        when(githubClient.searchRepositories(any(), any(), any(), any(), anyInt(), anyInt()))
            .thenThrow(new GitHubApiClientException("Bad request"));

        assertThatThrownBy(() -> underTest.findRepositories(request))
            .isInstanceOf(RepositorySearchException.class)
            .hasCauseInstanceOf(GitHubApiClientException.class);
    }

    @Test
    void shouldTranslateServerException() {
        SearchRequest request = new SearchRequest(
            "Java",
            LocalDate.of(2020, 1, 1),
            SortField.STARS,
            SortDirection.DESC,
            1,
            10
        );

        when(githubClient.searchRepositories(any(), any(), any(), any(), anyInt(), anyInt()))
            .thenThrow(new GitHubApiServerException("Internal server error"));

        assertThatThrownBy(() -> underTest.findRepositories(request))
            .isInstanceOf(RepositorySearchException.class)
            .hasCauseInstanceOf(GitHubApiServerException.class);
    }

    @Test
    void findRepositories_shouldTranslateGeneralApiException() {
        SearchRequest request = new SearchRequest(
            "Java",
            LocalDate.of(2020, 1, 1),
            SortField.STARS,
            SortDirection.DESC,
            1,
            10
        );

        when(githubClient.searchRepositories(any(), any(), any(), any(), anyInt(), anyInt()))
            .thenThrow(new GitHubApiException("Unknown error"));

        assertThatThrownBy(() -> underTest.findRepositories(request))
            .isInstanceOf(RepositorySearchException.class)
            .hasMessageContaining("Unknown error")
            .hasCauseInstanceOf(GitHubApiException.class);
    }

    @Test
    void findRepositories_shouldTranslateUnexpectedException() {
        SearchRequest request = new SearchRequest(
            "Java",
            LocalDate.of(2020, 1, 1),
            SortField.STARS,
            SortDirection.DESC,
            1,
            10
        );

        when(githubClient.searchRepositories(any(), any(), any(), any(), anyInt(), anyInt()))
            .thenThrow(new RuntimeException("Unexpected error"));

        assertThatThrownBy(() -> underTest.findRepositories(request))
            .isInstanceOf(RepositorySearchException.class)
            .hasCauseInstanceOf(RuntimeException.class);
    }
}
