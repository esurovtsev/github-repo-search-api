package com.grabduck.githubsearch.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryTest {

    private static final double DELTA = 0.001;
    private Instant now;
    
    @BeforeEach
    void setUp() {
        now = Instant.now();
    }

    @Test
    void popularityScoreWithAllComponents() {
        Repository repository = createRepository(now.minus(1, ChronoUnit.DAYS), 100, 20);

        int score = repository.popularityScore();

        // Expected: (100 * 0.7) + (20 * 0.2) + (10 / (1 + 1) * 0.1) = 70 + 4 + 0.5 = 74.5 = 75
        assertEquals(75, score);
    }

    @Test
    void popularityScoreWithAllNullComponents() {
        Repository repository = createRepository(null, null, null);

        int score = repository.popularityScore();

        // Expected: (100 * 0) + (20 * 0) + (0 * 0.1) = 0
        assertEquals(0, score);
    }

    @Test
    void popularityScoreWithNullStars() {
        Repository repository = createRepository(now.minus(1, ChronoUnit.DAYS), null, 20);

        int score = repository.popularityScore();

        // Expected: (0 * 0.7) + (20 * 0.2) + (10 / (1 + 1) * 0.1) = 0 + 4 + 0.5 = 4.5 = 5
        assertEquals(5, score);
    }

    @Test
    void popularityScoreWithNullForks() {
        Repository repository = createRepository(now.minus(1, ChronoUnit.DAYS), 100, null);

        int score = repository.popularityScore();

        // Expected: (100 * 0.7) + (0 * 0.2) + (10 / (1 + 1) * 0.1) = 70 + 0 + 0.5 = 70.5 = 71
        assertEquals(71, score);
    }

    @Test
    void popularityScoreWithNullUpdatedDate() {
        Repository repository = createRepository(null, 100, 20);

        int score = repository.popularityScore();

        // Expected: (100 * 0.7) + (20 * 0.2) + (0 * 0.1) = 70 + 4 + 0 = 74
        assertEquals(74, score);
    }

    private Repository createRepository(Instant updatedAt, Integer stars, Integer forks) {
        return new Repository(
            3081286L,
                "Tetris",
                "dtrupenn/Tetris",
                "https://api.github.com/repos/dtrupenn/Tetris",
                "A C implementation of Tetris using Pennsim through LC4",
                now.minus(100, ChronoUnit.DAYS),
                updatedAt,
                "Assembly",
                stars,
                forks
        );
    }
    

}
