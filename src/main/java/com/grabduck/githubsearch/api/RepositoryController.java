package com.grabduck.githubsearch.api;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.grabduck.githubsearch.domain.model.SearchRequest;
import com.grabduck.githubsearch.domain.model.SearchResults;
import com.grabduck.githubsearch.domain.model.SortDirection;
import com.grabduck.githubsearch.domain.model.SortField;
import com.grabduck.githubsearch.domain.service.RepositoryService;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;


import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/repositories")
@RequiredArgsConstructor
@Validated
public class RepositoryController {
    
    private final RepositoryService repositoryService;
    
    /**
     * Search for repositories with optional filtering and sorting.
     */
    @GetMapping
    public SearchResponseDto searchRepositories(
        @RequestParam(required = false) 
        String language,

        @RequestParam(required = false) 
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) 
        LocalDate createdSince,

        @RequestParam(required = false, defaultValue = "stars") 
        @Pattern(regexp = "stars|forks|updated", message = "Sort must be one of: stars, forks, updated") 
        String sort,

        @RequestParam(required = false, defaultValue = "desc") 
        @Pattern(regexp = "asc|desc", message = "Direction must be either asc or desc") 
        String direction,

        @RequestParam(required = true, defaultValue = "1") 
        @Min(value = 1, message = "Page must be greater than or equal to 1") 
        Integer page,

        @RequestParam(required = true, defaultValue = "10") 
        @Min(value = 1, message = "Size must be greater than or equal to 1")
        @Max(value = 100, message = "Size must be less than or equal to 100") 
        Integer size
    ) {
        SearchRequest searchRequest = new SearchRequest(
            language,
            createdSince,
            SortField.fromString(sort),
            SortDirection.fromString(direction),
            page,
            size
        );
        
        SearchResults results = repositoryService.searchRepositories(searchRequest);

        return SearchResponseDtoMapper.toDto(searchRequest, results);
    }
}
