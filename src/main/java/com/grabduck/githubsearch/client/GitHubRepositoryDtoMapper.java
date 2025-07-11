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
        
        Instant createdAt = dto.created_at() != null ? dto.created_at().toInstant(ZoneOffset.UTC) : null;
        Instant updatedAt = dto.updated_at() != null ? dto.updated_at().toInstant(ZoneOffset.UTC) : null;
        
        return new Repository(
                dto.id(),
                dto.name(),
                dto.full_name(),
                dto.html_url(),
                dto.description(),
                createdAt,
                updatedAt,
                dto.language(),
                dto.stargazers_count(),
                dto.forks_count()
        );
    }
}
