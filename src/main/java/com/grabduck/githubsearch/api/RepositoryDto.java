package com.grabduck.githubsearch.api;

import java.time.Instant;

public record RepositoryDto(
    Long id,
    String name,
    String fullName,
    String url,
    String description,
    Instant createdAt,
    Instant updatedAt,
    String language,
    Integer stargazersCount,
    Integer forksCount,
    Integer popularityScore
) {}
