package com.grabduck.githubsearch.domain.model;

import java.util.Arrays;
import static java.util.stream.Collectors.joining;

public enum SortField {
    STARS,
    FORKS,
    UPDATED;
    
    public String getValue() {
        return name().toLowerCase();
    }
    
    public static SortField fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Sort field must not be null");
        }
        try {
            return valueOf(value.toUpperCase());

        } catch (IllegalArgumentException e) {
            String allowed = Arrays.stream(values()).map(SortField::getValue).map(String::toLowerCase).collect(joining(", "));
            throw new IllegalArgumentException(
                "Invalid sort field: '%s'. Must be one of: %s".formatted(value, allowed)
            );
        }
    }
}
