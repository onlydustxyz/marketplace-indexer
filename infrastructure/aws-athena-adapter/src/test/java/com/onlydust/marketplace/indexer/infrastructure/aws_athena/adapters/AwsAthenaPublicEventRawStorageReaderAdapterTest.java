package com.onlydust.marketplace.indexer.infrastructure.aws_athena.adapters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.javafaker.Faker;
import com.onlydust.marketplace.indexer.infrastructure.aws_athena.AwsAthenaClient;

import software.amazon.awssdk.services.athena.model.Datum;
import software.amazon.awssdk.services.athena.model.GetQueryResultsResponse;
import software.amazon.awssdk.services.athena.model.ResultSet;
import software.amazon.awssdk.services.athena.model.Row;
import software.amazon.awssdk.services.athena.paginators.GetQueryResultsIterable;

class AwsAthenaPublicEventRawStorageReaderAdapterTest {

    private final AwsAthenaClient athenaClient = mock(AwsAthenaClient.class);
    private final Faker faker = new Faker();
    private AwsAthenaPublicEventRawStorageReaderAdapter adapter;

    @BeforeEach
    void setUp() {
        var properties = new AwsAthenaClient.Properties();
        properties.setBatchSize(2); // Small batch size for testing
        
        adapter = new AwsAthenaPublicEventRawStorageReaderAdapter(athenaClient, properties);
    }

    @Test
    void should_fetch_user_public_events() throws Exception {
        // Given
        final var userId = faker.random().nextLong();
        final var since = faker.date().past(1, TimeUnit.DAYS).toInstant().atZone(ZoneOffset.UTC);
        final var jsonEvent = """
                {
                    "id": "1234",
                    "type": "PushEvent",
                    "created_at": "2024-01-01T00:00:00Z",
                    "actor": {
                        "id": 123,
                        "login": "test-user"
                    },
                    "repo": {
                        "id": 456,
                        "name": "test/repo"
                    },
                    "payload": {
                        "push_id": 1234,
                        "size": 1,
                        "distinct_size": 1,
                        "ref": "refs/heads/main",
                        "head": "abc123",
                        "before": "def456",
                        "commits": []
                    }
                }""";

        final var results = mockResults(jsonEvent);
        when(athenaClient.query(anyString(), any(String[].class)))
                .thenReturn(CompletableFuture.completedFuture(results));

        // When
        final var events = adapter.userPublicEvents(userId, since).toList();

        // Then
        assertThat(events).hasSize(1);
        assertThat(events.get(0).id()).isEqualTo(1234);
        assertThat(events.get(0).type()).isEqualTo("PushEvent");
        assertThat(events.get(0).actor().getId()).isEqualTo(123L);
        assertThat(events.get(0).actor().getLogin()).isEqualTo("test-user");
    }

    @Test
    void should_fetch_all_public_events_in_batches() throws Exception {
        // Given
        final var timestamp = faker.date().past(1, TimeUnit.DAYS).toInstant().atZone(ZoneOffset.UTC);
        final var userIds = List.of(faker.random().nextLong(), faker.random().nextLong(), faker.random().nextLong());

        final var jsonEvent1 = mockResults("""
                {
                    "id": "1",
                    "type": "PushEvent",
                    "created_at": "2024-01-01T00:00:00Z",
                    "actor": {"id": 123, "login": "user1"},
                    "repo": {"id": 456, "name": "test/repo1"},
                    "payload": {
                        "push_id": 1,
                        "size": 1,
                        "distinct_size": 1,
                        "ref": "refs/heads/main",
                        "head": "abc123",
                        "before": "def456",
                        "commits": []
                    }
                }""");

        final var jsonEvent2 = mockResults("""
                {
                    "id": "2",
                    "type": "IssueEvent",
                    "created_at": "2024-01-01T01:00:00Z",
                    "actor": {"id": 456, "login": "user2"},
                    "repo": {"id": 789, "name": "test/repo2"},
                    "payload": {
                        "action": "opened",
                        "issue": {
                            "number": 1,
                            "title": "Test issue"
                        }
                    }
                }""");

        when(athenaClient.batchQuery(anyList()))
                .thenReturn(List.of(
                        CompletableFuture.completedFuture(jsonEvent1),
                        CompletableFuture.completedFuture(jsonEvent2)
                ));

        // When
        final var events = adapter.allPublicEvents(timestamp, userIds).toList();

        // Then
        assertThat(events).hasSize(2);
        assertThat(events).extracting("id").containsExactly(1L, 2L);
        assertThat(events).extracting("type").containsExactly("PushEvent", "IssueEvent");
        assertThat(events).extracting(e -> e.actor().getId())
                .containsExactly(123L, 456L);
    }

    private GetQueryResultsIterable mockResults(String... jsonEvents) {
        final var allRows = new ArrayList<Row>();

        // Add header row
        allRows.add(Row.builder()
                .data(Datum.builder().varCharValue("event").build())
                .build());

        // Add data rows
        allRows.addAll(List.of(jsonEvents).stream()
                .map(json -> Row.builder()
                        .data(Datum.builder().varCharValue(json).build())
                        .build())
                .toList());

        final var response = GetQueryResultsResponse.builder()
                .resultSet(ResultSet.builder()
                        .rows(allRows)
                        .build())
                .build();

        final var iterable = mock(GetQueryResultsIterable.class);
        when(iterable.stream()).thenReturn(List.of(response).stream());
        return iterable;
    }
} 