package com.grabduck.githubsearch.domain.model;

import java.time.Duration;
import java.time.Instant;

/**
 * Domain model representing a GitHub repository with calculated popularity score.
 * Implemented as an immutable record following DDD principles.
 */
public record Repository(
    Long id,
    String name,
    String fullName,
    String url,
    String description,
    Instant createdAt,
    Instant updatedAt,
    String language,
    Integer stargazersCount,
    Integer forksCount
) {
    private static final double STARS_WEIGHT = 0.7;
    private static final double FORKS_WEIGHT = 0.2;
    private static final double RECENCY_WEIGHT = 0.1;
    
    // Scaling factor for recency score to make it comparable to stars and forks
    private static final double RECENCY_SCALE_FACTOR = 10.0;
    
    /**
     * Calculates a popularity score based on stars, forks and update recency. Each component has it's own predefined weight.
     * 
     * @return A final score of the repository
     */
    public double popularityScore() {
        double starsScore = stargazersCount != null ? stargazersCount : 0;
        double forksScore = forksCount != null ? forksCount : 0;
        double recencyScore = getRecencyScore();
        
        return (starsScore * STARS_WEIGHT) + (forksScore * FORKS_WEIGHT) + (recencyScore * RECENCY_WEIGHT);
    }
    
    /**
     * Calculates a recency score using inverse proportionality (1/daysOld) so newer repositories get higher scores which is in line with stars and forks scores.
     * 
     * @return A recency score where higher values indicate more recently updated repositories
     */
    private double getRecencyScore() {
        if (updatedAt == null) {
            return 0.0;
        }
        
        long daysOld = Duration.between(updatedAt, Instant.now()).toDays();
        return RECENCY_SCALE_FACTOR / (daysOld + 1);
    }
}
