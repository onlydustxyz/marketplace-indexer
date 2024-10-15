package com.onlydust.marketplace.indexer.domain.services.jobs;

import com.github.javafaker.Faker;
import com.onlydust.marketplace.indexer.domain.models.CommitIndexingJobItem;
import com.onlydust.marketplace.indexer.domain.models.RateLimit;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.CommitIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.RateLimitService;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.CommitIndexingJobStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

import static org.mockito.Mockito.*;

class CommitIndexerJobServiceTest {
    private final static Faker faker = new Faker();

    private final CommitIndexer commitIndexer = mock(CommitIndexer.class);
    private final CommitIndexingJobStorage commitIndexingJobStorage = mock(CommitIndexingJobStorage.class);
    private final RateLimitService rateLimitService = mock(RateLimitService.class);
    private final CommitIndexerJobService commitIndexerJobService = new CommitIndexerJobService(commitIndexer, commitIndexingJobStorage, rateLimitService);

    private final List<CommitIndexingJobItem> commits = IntStream.range(0, 10)
            .mapToObj(i -> new CommitIndexingJobItem(faker.random().nextLong(), faker.random().hex()))
            .toList();

    @BeforeEach
    void setup() {
        reset(commitIndexer, commitIndexingJobStorage, rateLimitService);
    }

    @Test
    void should_index_all_commits_if_enough_rate_limit() {
        // Given
        when(rateLimitService.rateLimit()).thenReturn(new RateLimit(10000, Instant.now()));
        when(commitIndexingJobStorage.commitsForLeastIndexedUsers(9000)).thenReturn(commits);

        // When
        commitIndexerJobService.createJob().run();

        // Then
        commits.forEach(c -> verify(commitIndexer).indexCommit(c.repoId(), c.sha()));
    }

    @Test
    void should_index_some_commits_if_not_enough_rate_limit() {
        // Given
        when(rateLimitService.rateLimit()).thenReturn(new RateLimit(1005, Instant.now()));
        when(commitIndexingJobStorage.commitsForLeastIndexedUsers(5)).thenReturn(commits.subList(0, 5));

        // When
        commitIndexerJobService.createJob().run();

        // Then
        commits.subList(0, 5).forEach(c -> verify(commitIndexer).indexCommit(c.repoId(), c.sha()));
        verifyNoMoreInteractions(commitIndexer);
    }

    @Test
    void should_not_index_commits_if_no_more_rate_limit() {
        // Given
        when(rateLimitService.rateLimit()).thenReturn(new RateLimit(350, Instant.now()));
        when(commitIndexingJobStorage.commitsForLeastIndexedUsers(0)).thenReturn(List.of());

        // When
        commitIndexerJobService.createJob().run();

        // Then
        verifyNoMoreInteractions(commitIndexer);
    }
}