package com.grabduck.githubsearch.api;

import java.util.List;

public record SearchResponseDto(
    int total,
    List<RepositoryDto> items,
    MetadataDto metadata
) {
}
