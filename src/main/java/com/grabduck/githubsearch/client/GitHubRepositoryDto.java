package com.grabduck.githubsearch.client;

import java.time.LocalDateTime;

public record GitHubRepositoryDto(
    Long id,
    String name,
    String full_name,
    String html_url,
    String description,
    String language,
    int stargazers_count,
    int forks_count,
    LocalDateTime created_at,
    LocalDateTime updated_at
) {}
