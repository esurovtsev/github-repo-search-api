package com.grabduck.githubsearch.api;

import java.time.LocalDate;

public record MetadataDto(
    String language,
    LocalDate createdSince,
    String sort,
    String direction,
    int page,
    int size
) {
}