package com.grabduck.githubsearch.client;

import java.util.List;

public record GitHubSearchResponseDto(
    int total_count,
    boolean incomplete_results,
    List<GitHubRepositoryDto> items
) {}
