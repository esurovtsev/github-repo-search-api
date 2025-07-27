package com.grabduck.githubsearch.domain.model;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Domain model representing search criteria for repositories.
 */
public record SearchRequest(
    String language,
    LocalDate createdSince,
    
    @NotNull(message = "Sort field must not be null")
    SortField sort,
    
    @NotNull(message = "Sort direction must not be null")
    SortDirection direction,
    
    @NotNull(message = "Page must not be null")
    @Min(value = 1, message = "Page must be >= 1")
    Integer page,
    
    @NotNull(message = "Size must not be null")
    @Min(value = 1, message = "Size must be between 1 and 100")
    @Max(value = 100, message = "Size must be between 1 and 100")
    Integer size
) {
}
