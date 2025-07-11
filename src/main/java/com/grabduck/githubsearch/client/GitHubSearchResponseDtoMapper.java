package com.grabduck.githubsearch.client;

import com.grabduck.githubsearch.domain.model.Repository;

import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@UtilityClass
public class GitHubSearchResponseDtoMapper {
    
    public static List<Repository> toModel(List<GitHubRepositoryDto> dtos) {
        if (dtos == null) {
            return List.of();
        }
        
        return dtos.stream()
                .map(GitHubRepositoryDtoMapper::toModel)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
