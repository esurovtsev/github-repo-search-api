package com.grabduck.githubsearch.client;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record GitHubRepositoryDto(
    Long id,
    String name,
    @JsonProperty("full_name") 
    String fullName,
    @JsonProperty("html_url") 
    String htmlUrl,
    String description,
    String language,
    @JsonProperty("stargazers_count") 
    int stargazersCount,
    @JsonProperty("forks_count") 
    int forksCount,
    @JsonProperty("created_at") 
    LocalDateTime createdAt,
    @JsonProperty("updated_at") 
    LocalDateTime updatedAt
) {}
