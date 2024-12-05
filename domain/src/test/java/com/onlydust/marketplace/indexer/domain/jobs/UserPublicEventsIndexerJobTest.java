package com.onlydust.marketplace.indexer.domain.jobs;

import com.github.javafaker.Faker;
import com.onlydust.marketplace.indexer.domain.models.raw.RawAccount;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.UserPublicEventsIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.UserPublicEventsIndexingJobStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class UserPublicEventsIndexerJobTest {
    private final Faker faker = new Faker();
    private final UserPublicEventsIndexer userPublicEventsIndexer = mock(UserPublicEventsIndexer.class);
    private final UserPublicEventsIndexingJobStorage userPublicEventsIndexingJobStorage = mock(UserPublicEventsIndexingJobStorage.class);
    private final RawStorageReader rawStorageReader = mock(RawStorageReader.class);

    @BeforeEach
    void setUp() {
        reset(userPublicEventsIndexer, userPublicEventsIndexingJobStorage, rawStorageReader);
    }

    @Nested
    class GivenSingleUserSignup {
        private final ZonedDateTime userCreationDate = faker.date().birthday().toInstant().atZone(ZoneOffset.UTC);

        private final RawAccount user = RawAccount.builder()
                .id(faker.random().nextLong())
                .createdAt(userCreationDate.toString())
                .build();

        final UserPublicEventIndexerJob userPublicEventIndexerJob = new UserPublicEventIndexerJob(userPublicEventsIndexer,
                user.getId(),
                userPublicEventsIndexingJobStorage,
                rawStorageReader);

        @Test
        void should_not_index_non_existing_user() {
            // Given
            when(rawStorageReader.user(user.getId())).thenReturn(Optional.empty());
            when(userPublicEventsIndexingJobStorage.lastEventTimestamp(user.getId())).thenReturn(Optional.empty());

            // When
            userPublicEventIndexerJob.execute();

            // Then
            verifyNoInteractions(userPublicEventsIndexer);
        }

        @Test
        void should_index_new_user_from_creation_date() {
            // Given
            when(rawStorageReader.user(user.getId())).thenReturn(Optional.of(user));
            when(userPublicEventsIndexingJobStorage.lastEventTimestamp(user.getId())).thenReturn(Optional.empty());

            // When
            userPublicEventIndexerJob.execute();

            // Then
            verify(userPublicEventsIndexingJobStorage).startJob(user.getId());
            verify(userPublicEventsIndexer).indexUser(user.getId(), userCreationDate);
            verify(userPublicEventsIndexingJobStorage).endJob(user.getId());
        }

        @Test
        void should_index_existing_user_from_last_event() {
            // Given
            final var lastEventTimestamp = ZonedDateTime.now();
            when(rawStorageReader.user(user.getId())).thenReturn(Optional.of(user));
            when(userPublicEventsIndexingJobStorage.lastEventTimestamp(user.getId())).thenReturn(Optional.of(lastEventTimestamp));

            // When
            userPublicEventIndexerJob.execute();

            // Then
            verify(userPublicEventsIndexingJobStorage).startJob(user.getId());
            verify(userPublicEventsIndexer).indexUser(user.getId(), lastEventTimestamp);
            verify(userPublicEventsIndexingJobStorage).endJob(user.getId());
        }

        @Test
        void should_fail_upon_exception() {
            // Given
            when(rawStorageReader.user(user.getId())).thenReturn(Optional.of(user));
            when(userPublicEventsIndexingJobStorage.lastEventTimestamp(user.getId())).thenReturn(Optional.empty());
            doThrow(new RuntimeException("Failed to index user")).when(userPublicEventsIndexer).indexUser(user.getId(), userCreationDate);

            // When
            assertThatThrownBy(userPublicEventIndexerJob::execute)
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Failed to index user");

            // Then
            verify(userPublicEventsIndexingJobStorage).startJob(user.getId());
            verify(userPublicEventsIndexer).indexUser(user.getId(), userCreationDate);
            verify(userPublicEventsIndexingJobStorage).failJob(user.getId());
        }
    }
}
