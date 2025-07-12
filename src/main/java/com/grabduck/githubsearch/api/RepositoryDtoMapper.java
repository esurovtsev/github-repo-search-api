package com.grabduck.githubsearch.api;

import com.grabduck.githubsearch.domain.model.Repository;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RepositoryDtoMapper {
    public static RepositoryDto toDto(@NonNull Repository repository) {
        return new RepositoryDto(
            repository.id(),
            repository.name(),
            repository.fullName(),
            repository.url(),
            repository.description(),
            repository.createdAt(),
            repository.updatedAt(),
            repository.language(),
            repository.stargazersCount(),
            repository.forksCount(),
            repository.popularityScore()
        );
    }
    
}
