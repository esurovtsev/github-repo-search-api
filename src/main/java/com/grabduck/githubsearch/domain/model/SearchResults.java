package com.grabduck.githubsearch.domain.model;

import java.util.List;

/**
 * Domain model representing repository search results.
 */
public record SearchResults(
    int total,
    List<Repository> items
) {}
