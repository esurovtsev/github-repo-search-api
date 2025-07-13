package com.grabduck.githubsearch;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.grabduck.githubsearch.domain.exceptions.RepositorySearchException;
import com.grabduck.githubsearch.domain.model.Repository;
import com.grabduck.githubsearch.domain.model.SearchRequest;
import com.grabduck.githubsearch.domain.model.SearchResults;
import com.grabduck.githubsearch.domain.service.RepositoryProvider;

@SpringBootTest
@AutoConfigureMockMvc
public class RepositorySearchIT {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private RepositoryProvider repositoryProvider;
    
    @BeforeEach
    void setUp() {
        when(repositoryProvider.findRepositories(any(SearchRequest.class)))
            .thenReturn(createSearchResults());
    }

    @Test
    void basicSearchWithDefaultParameters() throws Exception {
        SearchResults expected = createSearchResults();

        mockMvc.perform(get("/api/repositories")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(expected.total()))
                .andExpect(jsonPath("$.items.length()").value(expected.items().size()))
                .andExpect(jsonPath("$.items[0].name").value(expected.items().get(0).name()))
                .andExpect(jsonPath("$.items[0].popularityScore").isNumber());
    }
    
    @Test
    void fillsMetadata() throws Exception {        
        mockMvc.perform(get("/api/repositories")
                .param("language", "java")
                .param("sort", "forks")
                .param("direction", "asc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metadata.language").value("java"))
                .andExpect(jsonPath("$.metadata.sort").value("forks"))
                .andExpect(jsonPath("$.metadata.direction").value("asc"));
    }
    
    @Test
    void handlesErrorResponseFromGitHub() throws Exception {
        doThrow(new RepositorySearchException("API rate limit exceeded. Please try again later."))
            .when(repositoryProvider).findRepositories(any(SearchRequest.class));
            
        mockMvc.perform(get("/api/repositories")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.title").value("Repository Search Failed"))
                .andExpect(jsonPath("$.status").value(503));
    }
    
    @Test
    void handlesBadRequestErrors() throws Exception {
        mockMvc.perform(get("/api/repositories")
                .param("sort", "popularity") // Invalid sort parameter
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Error"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").exists());
    }
    
    private SearchResults createSearchResults() {
        return new SearchResults(3, List.of(
            new Repository(
                28457823L,
                "freeCodeCamp",
                "freeCodeCamp/freeCodeCamp",
                "https://github.com/freeCodeCamp/freeCodeCamp",
                "freeCodeCamp.org's open-source codebase and curriculum. Learn math, programming, and computer science for free.",
                Instant.parse("2014-12-24T17:49:19Z"),
                Instant.parse("2025-07-13T09:28:25Z"),
                "TypeScript",
                422771,
                40730
            ),
            new Repository(
                132750724L,
                "build-your-own-x",
                "codecrafters-io/build-your-own-x",
                "https://github.com/codecrafters-io/build-your-own-x",
                "Master programming by recreating your favorite technologies from scratch.",
                Instant.parse("2018-05-09T12:03:18Z"),
                Instant.parse("2025-07-13T09:29:45Z"),
                "Markdown",
                399542,
                37306
            ),
            new Repository(
                13491895L,
                "free-programming-books",
                "EbookFoundation/free-programming-books",
                "https://github.com/EbookFoundation/free-programming-books",
                ":books: Freely available programming books",
                Instant.parse("2013-10-11T06:50:37Z"),
                Instant.parse("2025-07-13T09:26:39Z"),
                "Python",
                362740,
                63737
            )
        ));
    }
}
