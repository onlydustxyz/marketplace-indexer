package com.onlydust.marketplace.indexer.infrastructure.aws_athena;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import software.amazon.awssdk.services.athena.AthenaClient;
import software.amazon.awssdk.services.athena.model.*;
import software.amazon.awssdk.services.athena.paginators.GetQueryResultsIterable;

class AwsAthenaClientTest {

    private final AthenaClient athenaClient = mock(AthenaClient.class);
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(4);
    private AwsAthenaClient client;

    @BeforeEach
    void setUp() {
        var properties = new AwsAthenaClient.Properties();
        properties.setPollingInterval(1);
        properties.setParallelQueries(3); // Allow 3 concurrent queries

        client = new AwsAthenaClient(executor, properties, athenaClient);
    }

    @Test
    void should_respect_parallel_query_limit() throws Exception {
        // Given
        final var totalQueries = 5;
        final var queriesStarted = new CountDownLatch(3); // Should only start 3 queries initially
        final var testComplete = new CountDownLatch(1);

        when(athenaClient.startQueryExecution(any(StartQueryExecutionRequest.class)))
                .thenAnswer(invocation -> {
                    Thread.sleep(100); // Add a small delay to ensure we can observe the concurrency
                    queriesStarted.countDown();
                    return StartQueryExecutionResponse.builder()
                            .queryExecutionId("query-" + System.nanoTime())
                            .build();
                });

        when(athenaClient.getQueryExecution(any(GetQueryExecutionRequest.class)))
                .thenReturn(GetQueryExecutionResponse.builder()
                        .queryExecution(QueryExecution.builder()
                                .status(QueryExecutionStatus.builder()
                                        .state(QueryExecutionState.SUCCEEDED)
                                        .build())
                                .build())
                        .build());

        when(athenaClient.getQueryResultsPaginator(any(GetQueryResultsRequest.class)))
                .thenReturn(mock(GetQueryResultsIterable.class));

        // When
        final var queries = IntStream.range(0, totalQueries)
                .mapToObj(i -> new AwsAthenaClient.BatchQuery("SELECT * FROM table" + i, new String[]{}))
                .toList();

        final var futures = client.batchQuery(queries);

        // Then
        // Verify that only 3 queries started initially (due to semaphore limit)
        assertThat(queriesStarted.await(5, TimeUnit.SECONDS)).isTrue();
        
        // Verify all queries eventually complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenRun(testComplete::countDown);
        
        assertThat(testComplete.await(10, TimeUnit.SECONDS)).isTrue();

        // Verify the total number of queries executed
        verify(athenaClient, times(totalQueries)).startQueryExecution(any(StartQueryExecutionRequest.class));
    }

    @Test
    void should_release_semaphore_on_failure() throws Exception {
        // Given
        when(athenaClient.startQueryExecution(any(StartQueryExecutionRequest.class)))
                .thenThrow(new RuntimeException("Query failed"));

        // When
        final var future = client.query("SELECT 1");

        // Then
        assertThat(future)
                .failsWithin(Duration.ofSeconds(5))
                .withThrowableOfType(ExecutionException.class)
                .withCauseInstanceOf(RuntimeException.class);

        // Verify semaphore was released
        assertThat(client.query("SELECT 1")).isNotNull();
    }
}