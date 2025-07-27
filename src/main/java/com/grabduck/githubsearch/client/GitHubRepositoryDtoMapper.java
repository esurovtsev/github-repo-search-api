package com.grabduck.githubsearch.client;

import com.grabduck.githubsearch.domain.model.Repository;

import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.ZoneOffset;

@UtilityClass
public class GitHubRepositoryDtoMapper {
    
    public static Repository toModel(GitHubRepositoryDto dto) {
        if (dto == null) {
            return null;
        }
        
        Instant createdAt = dto.createdAt() != null ? dto.createdAt().toInstant(ZoneOffset.UTC) : null;
        Instant updatedAt = dto.updatedAt() != null ? dto.updatedAt().toInstant(ZoneOffset.UTC) : null;
        
        return new Repository(
                dto.id(),
                dto.name(),
                dto.fullName(),
                dto.htmlUrl(),
                dto.description(),
                createdAt,
                updatedAt,
                dto.language(),
                dto.stargazersCount(),
                dto.forksCount()
        );
    }
}
