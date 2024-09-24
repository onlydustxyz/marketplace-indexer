package com.onlydust.marketplace.indexer.domain.jobs;

import com.github.javafaker.Faker;
import com.onlydust.marketplace.indexer.domain.models.raw.RawAccount;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.UserStatsIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.UserStatsIndexingJobStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;

class UserStatsIndexerJobTest {
    private final Faker faker = new Faker();
    private final ZonedDateTime userCreationDate = faker.date().birthday().toInstant().atZone(ZoneOffset.UTC);
    private final RawAccount user = RawAccount.builder()
            .id(faker.random().nextLong())
            .createdAt(userCreationDate.toString())
            .build();
    private final UserStatsIndexer userStatsIndexer = mock(UserStatsIndexer.class);
    private final UserStatsIndexingJobStorage userStatsIndexingJobStorage = mock(UserStatsIndexingJobStorage.class);
    private final RawStorageReader rawStorageReader = mock(RawStorageReader.class);
    final UserStatsIndexerJob userStatsIndexerJob = new UserStatsIndexerJob(
            userStatsIndexer,
            Set.of(user.getId()),
            userStatsIndexingJobStorage,
            rawStorageReader);

    @BeforeEach
    void setUp() {
        reset(userStatsIndexer, userStatsIndexingJobStorage, rawStorageReader);
    }

    @Test
    void should_not_index_non_existing_user() {
        // Given
        when(rawStorageReader.user(user.getId())).thenReturn(Optional.empty());

        // When
        userStatsIndexerJob.execute();

        // Then
        verifyNoInteractions(userStatsIndexingJobStorage);
        verifyNoInteractions(userStatsIndexer);
    }

    @Test
    void should_index_new_user_from_creation_date() {
        // Given
        when(rawStorageReader.user(user.getId())).thenReturn(Optional.of(user));
        when(userStatsIndexingJobStorage.lastEventTimestamp(user.getId())).thenReturn(Optional.empty());

        // When
        userStatsIndexerJob.execute();

        // Then
        verify(userStatsIndexingJobStorage).startJob(user.getId());
        verify(userStatsIndexer).indexUser(user.getId(), userCreationDate);
        verify(userStatsIndexingJobStorage).endJob(user.getId());
    }

    @Test
    void should_index_existing_user_from_last_event() {
        // Given
        final var lastEventTimestamp = ZonedDateTime.now();
        when(rawStorageReader.user(user.getId())).thenReturn(Optional.of(user));
        when(userStatsIndexingJobStorage.lastEventTimestamp(user.getId())).thenReturn(Optional.of(lastEventTimestamp));

        // When
        userStatsIndexerJob.execute();

        // Then
        verify(userStatsIndexingJobStorage).startJob(user.getId());
        verify(userStatsIndexer).indexUser(user.getId(), lastEventTimestamp);
        verify(userStatsIndexingJobStorage).endJob(user.getId());
    }

    @Test
    void should_fail_upon_exception() {
        // Given
        when(rawStorageReader.user(user.getId())).thenReturn(Optional.of(user));
        when(userStatsIndexingJobStorage.lastEventTimestamp(user.getId())).thenReturn(Optional.empty());
        doThrow(new RuntimeException("Failed to index user")).when(userStatsIndexer).indexUser(user.getId(), userCreationDate);

        // When
        userStatsIndexerJob.execute();

        // Then
        verify(userStatsIndexingJobStorage).startJob(user.getId());
        verify(userStatsIndexer).indexUser(user.getId(), userCreationDate);
        verify(userStatsIndexingJobStorage).failJob(user.getId());
    }
}
