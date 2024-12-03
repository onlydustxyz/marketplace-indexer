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
import java.util.Set;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toSet;
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
                Set.of(user.getId()),
                userPublicEventsIndexingJobStorage,
                rawStorageReader);

        @Test
        void should_not_index_non_existing_user() {
            // Given
            when(rawStorageReader.user(user.getId())).thenReturn(Optional.empty());
            when(userPublicEventsIndexingJobStorage.lastEventTimestamp(Set.of(user.getId()))).thenReturn(Optional.empty());

            // When
            userPublicEventIndexerJob.execute();

            // Then
            verifyNoInteractions(userPublicEventsIndexer);
        }

        @Test
        void should_index_new_user_from_creation_date() {
            // Given
            when(rawStorageReader.user(user.getId())).thenReturn(Optional.of(user));
            when(userPublicEventsIndexingJobStorage.lastEventTimestamp(Set.of(user.getId()))).thenReturn(Optional.empty());

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
            when(userPublicEventsIndexingJobStorage.lastEventTimestamp(Set.of(user.getId()))).thenReturn(Optional.of(lastEventTimestamp));

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
            when(userPublicEventsIndexingJobStorage.lastEventTimestamp(Set.of(user.getId()))).thenReturn(Optional.empty());
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

    @Nested
    class GivenManyUsersRefresh {
        private final ZonedDateTime userCreationDate = faker.date().birthday().toInstant().atZone(ZoneOffset.UTC);
        private final Set<RawAccount> users = IntStream.range(0, 10)
                .mapToObj(i -> RawAccount.builder()
                        .id(faker.random().nextLong())
                        .createdAt(userCreationDate.minusDays(i).toString())
                        .build())
                .collect(toSet());
        final Set<Long> userIds = users.stream().map(RawAccount::getId).collect(toSet());

        final UserPublicEventIndexerJob userPublicEventIndexerJob = new UserPublicEventIndexerJob(userPublicEventsIndexer,
                userIds,
                userPublicEventsIndexingJobStorage,
                rawStorageReader);

        @Test
        void should_not_index_users_for_the_first_time() {
            // Given
            when(userPublicEventsIndexingJobStorage.lastEventTimestamp(userIds)).thenReturn(Optional.empty());

            // When
            userPublicEventIndexerJob.execute();

            // Then
//            userIds.forEach(id -> verify(userPublicEventsIndexingJobStorage).failJob(id));
            verifyNoInteractions(userPublicEventsIndexer);
        }

        @Test
        void should_index_users_from_last_event() {
            // Given
            final var lastEventTimestamp = ZonedDateTime.now().minusHours(2);
            when(userPublicEventsIndexingJobStorage.lastEventTimestamp(userIds)).thenReturn(Optional.of(lastEventTimestamp));

            // When
            userPublicEventIndexerJob.execute();

            // Then
            userIds.forEach(id -> verify(userPublicEventsIndexingJobStorage).startJob(id));
            verify(userPublicEventsIndexer).indexUsers(userIds, lastEventTimestamp);
            userIds.forEach(id -> verify(userPublicEventsIndexingJobStorage).endJob(id));
        }

        @Test
        void should_fail_upon_exception() {
            // Given
            final var lastEventTimestamp = ZonedDateTime.now().minusHours(2);
            when(userPublicEventsIndexingJobStorage.lastEventTimestamp(userIds)).thenReturn(Optional.of(lastEventTimestamp));
            doThrow(new RuntimeException("Failed to index user")).when(userPublicEventsIndexer).indexUsers(userIds, lastEventTimestamp);

            // When
            assertThatThrownBy(userPublicEventIndexerJob::execute)
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Failed to index user");

            // Then
            userIds.forEach(id -> verify(userPublicEventsIndexingJobStorage).startJob(id));
            verify(userPublicEventsIndexer).indexUsers(userIds, lastEventTimestamp);
            userIds.forEach(id -> verify(userPublicEventsIndexingJobStorage).failJob(id));
        }
    }
}
