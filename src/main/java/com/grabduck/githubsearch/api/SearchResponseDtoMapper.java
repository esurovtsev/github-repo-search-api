package com.grabduck.githubsearch.api;

import java.util.stream.Collectors;

import com.grabduck.githubsearch.domain.model.SearchRequest;
import com.grabduck.githubsearch.domain.model.SearchResults;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SearchResponseDtoMapper {
    public static SearchResponseDto toDto(@NonNull SearchRequest searchRequest, @NonNull SearchResults results) {
        return new SearchResponseDto(
            results.total(),
            results.items().stream()
                .map(RepositoryDtoMapper::toDto)
                .collect(Collectors.toList()),
            MetadataDtoMapper.toDto(searchRequest)
        );
    }
}
