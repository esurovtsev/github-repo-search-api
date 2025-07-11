package com.grabduck.githubsearch.domain.model;

import java.time.LocalDate;

/**
 * Domain model representing search criteria for repositories.
 */
public record SearchRequest(
    String language,
    LocalDate createdSince,
    SortField sort,
    SortDirection direction,
    Integer page,
    Integer size
) {
    
    public SearchRequest {
        if (sort == null) throw new IllegalArgumentException("Sort field must not be null");
        if (direction == null) throw new IllegalArgumentException("Sort direction must not be null");
        if (page == null) throw new IllegalArgumentException("Page must not be null");
        if (size == null) throw new IllegalArgumentException("Size must not be null");

        if (page < 1) throw new IllegalArgumentException("Page must be >= 1");
        if (size < 1 || size > 100) throw new IllegalArgumentException("Size must be between 1 and 100");
    }
}
