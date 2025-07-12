package com.grabduck.githubsearch.api;

import com.grabduck.githubsearch.domain.model.SearchRequest;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MetadataDtoMapper {
    public static MetadataDto toDto(@NonNull SearchRequest searchRequest) {
        return new MetadataDto(
            searchRequest.language(),
            searchRequest.createdSince(),
            searchRequest.sort().getValue(),
            searchRequest.direction().getValue(),
            searchRequest.page(),
            searchRequest.size()
        );
    }
}
