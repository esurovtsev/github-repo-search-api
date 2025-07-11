package com.grabduck.githubsearch.domain.model;

import java.util.Arrays;
import static java.util.stream.Collectors.joining;

public enum SortDirection {
    ASC,
    DESC;
    
    public String getValue() {
        return name().toLowerCase();
    }
    
    public static SortDirection fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Sort direction must not be null");
        }
        try {
            return valueOf(value.toUpperCase());

        } catch (IllegalArgumentException e) {
            String allowed = Arrays.stream(values()).map(SortDirection::getValue).map(String::toLowerCase).collect(joining(", "));
            throw new IllegalArgumentException(
                "Invalid sort direction: '%s'. Must be one of: %s".formatted(value, allowed)
            );
        }
    }
}
