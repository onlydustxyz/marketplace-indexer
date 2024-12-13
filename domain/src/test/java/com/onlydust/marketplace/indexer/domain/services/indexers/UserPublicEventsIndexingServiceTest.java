package com.onlydust.marketplace.indexer.domain.services.indexers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.github.javafaker.Faker;
import com.onlydust.marketplace.indexer.domain.models.raw.RawAccount;
import com.onlydust.marketplace.indexer.domain.models.raw.RawIssue;
import com.onlydust.marketplace.indexer.domain.models.raw.RawPullRequest;
import com.onlydust.marketplace.indexer.domain.models.raw.RawRepo;
import com.onlydust.marketplace.indexer.domain.models.raw.public_events.RawPublicEvent;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.IssueIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.PullRequestIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.UserPublicEventsIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.UserPublicEventsIndexingJobStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.PublicEventRawStorageReader;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageReader;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageWriter;
import com.onlydust.marketplace.indexer.domain.stubs.RawStorageWriterStub;

class UserPublicEventsIndexingServiceTest {
    private final Faker faker = new Faker();
    private final PublicEventRawStorageReader publicEventRawStorageReader = mock(PublicEventRawStorageReader.class);
    private final UserPublicEventsIndexingJobStorage userPublicEventsIndexingJobStorage = mock(UserPublicEventsIndexingJobStorage.class);
    private final RawStorageWriter rawStorageWriter = mock(RawStorageWriter.class);
    private final RawStorageReader rawStorageReader = mock(RawStorageReader.class);

    private final PullRequestIndexer pullRequestIndexer = mock(PullRequestIndexer.class);
    private final IssueIndexer issueIndexer = mock(IssueIndexer.class);

    private final UserPublicEventsIndexer indexer = new UserPublicEventsIndexingService(
            publicEventRawStorageReader,
            userPublicEventsIndexingJobStorage,
            rawStorageWriter,
            rawStorageReader,
            pullRequestIndexer,
            issueIndexer
    );

    private final RawAccount onlyDust = RawStorageWriterStub.load("/github/users/onlyDust.json", RawAccount.class);
    private final RawAccount antho = RawStorageWriterStub.load("/github/users/anthony.json", RawAccount.class);
    private final RawRepo marketplaceApi = RawStorageWriterStub.load("/github/repos/marketplace-api.json", RawRepo.class);
    private final RawPublicEvent[] events = RawStorageWriterStub.load("/github/public_events/antho_last_events.json", RawPublicEvent[].class);

    @BeforeEach
    void setUp() {
        reset(publicEventRawStorageReader, userPublicEventsIndexingJobStorage, rawStorageWriter, rawStorageReader, pullRequestIndexer, issueIndexer);

        when(rawStorageReader.user(onlyDust.getId())).thenReturn(Optional.of(onlyDust));
        when(rawStorageReader.repo(marketplaceApi.getId())).thenReturn(Optional.of(marketplaceApi));
    }

    @Nested
    class SingleUserIndexing {
        final Long userId = faker.random().nextLong();
        final ZonedDateTime since = faker.date().birthday().toInstant().atZone(ZoneOffset.UTC);

        @Test
        void should_index_user_since_timestamp() {
            // Given
            when(publicEventRawStorageReader.userPublicEvents(userId, since)).thenReturn(Arrays.stream(events));

            // When
            indexer.indexUser(userId, since);

            // Then
            final var pullRequestCaptor = ArgumentCaptor.forClass(RawPullRequest.class);
            verify(rawStorageWriter, times(21)).savePullRequest(pullRequestCaptor.capture());
            assertThat(pullRequestCaptor.getAllValues())
                    .extracting(RawPullRequest::getId)
                    .containsExactly(2105660879L, 2105333230L, 2105333230L, 2105028050L, 2105042045L, 2105028050L, 2104949872L, 2104949872L, 2102752678L,
                            2103753475L, 2102053983L, 2101773219L, 2102752678L, 2101773219L, 2101400893L, 2101400893L, 2099172257L, 2099486556L, 2099172257L,
                            2098542863L, 2098542863L);

            verify(pullRequestIndexer, times(2)).indexPullRequest("onlydustxyz", "marketplace-api", 1170L);
            verify(pullRequestIndexer, times(2)).indexPullRequest("onlydustxyz", "marketplace-api", 1172L);
            verify(pullRequestIndexer, times(1)).indexPullRequest("onlydustxyz", "marketplace-api", 1174L);
            verify(pullRequestIndexer, times(2)).indexPullRequest("onlydustxyz", "marketplace-api", 1177L);
            verify(pullRequestIndexer, times(2)).indexPullRequest("onlydustxyz", "marketplace-api", 1178L);
            verify(pullRequestIndexer, times(1)).indexPullRequest("onlydustxyz", "marketplace-api", 1180L);
            verify(pullRequestIndexer, times(2)).indexPullRequest("onlydustxyz", "marketplace-api", 1181L);
            verify(pullRequestIndexer, times(1)).indexPullRequest("onlydustxyz", "marketplace-api", 1183L);
            verify(pullRequestIndexer, times(2)).indexPullRequest("onlydustxyz", "marketplace-api", 1184L);
            verify(pullRequestIndexer, times(2)).indexPullRequest("onlydustxyz", "marketplace-api", 1185L);
            verify(pullRequestIndexer, times(1)).indexPullRequest("onlydustxyz", "marketplace-api", 1186L);
            verify(pullRequestIndexer, times(2)).indexPullRequest("onlydustxyz", "marketplace-api", 1187L);
            verify(pullRequestIndexer, times(1)).indexPullRequest("onlydustxyz", "marketplace-api", 1190L);

            final var issueCaptor = ArgumentCaptor.forClass(RawIssue.class);
            verify(rawStorageWriter).saveIssue(eq(marketplaceApi.getId()), issueCaptor.capture());
            assertThat(issueCaptor.getAllValues())
                    .extracting(RawIssue::getId)
                    .containsExactly(2693324012L);

            verify(issueIndexer).indexIssue("onlydustxyz", "marketplace-api", 1548L);
        }
    }

    @Nested
    class GivenManyUsersRefresh {
        final ZonedDateTime timestamp = faker.date().birthday().toInstant().atZone(ZoneOffset.UTC);
        final Set<Long> userIds = Set.of(antho.getId());

        @Test
        void should_index_all_users_for_given_timestamp() {
            // Given
            when(userPublicEventsIndexingJobStorage.all()).thenReturn(userIds);
            when(publicEventRawStorageReader.allPublicEvents(timestamp, userIds.stream().toList())).thenReturn(Arrays.stream(events));

            // When
            indexer.indexAllUsers(timestamp);

            // Then
            final var pullRequestCaptor = ArgumentCaptor.forClass(RawPullRequest.class);
            verify(rawStorageWriter, times(21)).savePullRequest(pullRequestCaptor.capture());
            assertThat(pullRequestCaptor.getAllValues())
                    .extracting(RawPullRequest::getId)
                    .containsExactly(2105660879L, 2105333230L, 2105333230L, 2105028050L, 2105042045L, 2105028050L, 2104949872L, 2104949872L, 2102752678L,
                            2103753475L, 2102053983L, 2101773219L, 2102752678L, 2101773219L, 2101400893L, 2101400893L, 2099172257L, 2099486556L, 2099172257L,
                            2098542863L, 2098542863L);

            verify(pullRequestIndexer, times(2)).indexPullRequest("onlydustxyz", "marketplace-api", 1170L);
            verify(pullRequestIndexer, times(2)).indexPullRequest("onlydustxyz", "marketplace-api", 1172L);
            verify(pullRequestIndexer, times(1)).indexPullRequest("onlydustxyz", "marketplace-api", 1174L);
            verify(pullRequestIndexer, times(2)).indexPullRequest("onlydustxyz", "marketplace-api", 1177L);
            verify(pullRequestIndexer, times(2)).indexPullRequest("onlydustxyz", "marketplace-api", 1178L);
            verify(pullRequestIndexer, times(1)).indexPullRequest("onlydustxyz", "marketplace-api", 1180L);
            verify(pullRequestIndexer, times(2)).indexPullRequest("onlydustxyz", "marketplace-api", 1181L);
            verify(pullRequestIndexer, times(1)).indexPullRequest("onlydustxyz", "marketplace-api", 1183L);
            verify(pullRequestIndexer, times(2)).indexPullRequest("onlydustxyz", "marketplace-api", 1184L);
            verify(pullRequestIndexer, times(2)).indexPullRequest("onlydustxyz", "marketplace-api", 1185L);
            verify(pullRequestIndexer, times(1)).indexPullRequest("onlydustxyz", "marketplace-api", 1186L);
            verify(pullRequestIndexer, times(2)).indexPullRequest("onlydustxyz", "marketplace-api", 1187L);
            verify(pullRequestIndexer, times(1)).indexPullRequest("onlydustxyz", "marketplace-api", 1190L);
        }

        @Test
        void should_fail_upon_exception() {
            // Given
            when(userPublicEventsIndexingJobStorage.all()).thenReturn(userIds);
            when(publicEventRawStorageReader.allPublicEvents(timestamp, userIds.stream().toList())).thenReturn(Arrays.stream(events));
            doThrow(new RuntimeException("Failed to index pull request")).when(pullRequestIndexer).indexPullRequest(any(), any(), anyLong());

            // When
            assertThatThrownBy(() -> indexer.indexAllUsers(timestamp))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Failed to index pull request");
        }
    }
}