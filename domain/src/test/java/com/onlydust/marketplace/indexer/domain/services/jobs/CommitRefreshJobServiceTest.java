package com.onlydust.marketplace.indexer.domain.services.jobs;

import com.github.javafaker.Faker;
import com.onlydust.marketplace.indexer.domain.models.CommitIndexingJobItem;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.CommitIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.UserFileExtensionStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.CommitIndexingJobStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

class CommitRefreshJobServiceTest {

    private final static Faker faker = new Faker();
    private final CommitIndexingJobStorage commitIndexingJobStorage = mock(CommitIndexingJobStorage.class);
    private final CommitIndexer commitIndexer = mock(CommitIndexer.class);
    private final UserFileExtensionStorage userFileExtensionStorage = mock(UserFileExtensionStorage.class);
    private final CommitRefreshJobService commitRefreshJobService = new CommitRefreshJobService(commitIndexingJobStorage, commitIndexer,
            userFileExtensionStorage);

    @BeforeEach
    void setup() {
        reset(commitIndexingJobStorage, commitIndexer, userFileExtensionStorage);
    }

    @Test
    void should_refresh_all_commits() {
        // Given
        final var commits = List.of(
                new CommitIndexingJobItem(faker.random().nextLong(), faker.random().hex()),
                new CommitIndexingJobItem(faker.random().nextLong(), faker.random().hex()),
                new CommitIndexingJobItem(faker.random().nextLong(), faker.random().hex())
        );

        when(commitIndexingJobStorage.all()).thenReturn(commits);

        // When
        commitRefreshJobService.createJob().run();

        // Then
        verify(userFileExtensionStorage).clear();
        commits.forEach(c -> verify(commitIndexer).indexCommit(c.repoId(), c.sha()));
    }
}