package com.onlydust.marketplace.indexer.infrastructure.aws_athena;

import com.onlydust.marketplace.indexer.domain.exception.OnlyDustException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.athena.AthenaClient;
import software.amazon.awssdk.services.athena.model.*;
import software.amazon.awssdk.services.athena.paginators.GetQueryResultsIterable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AwsAthenaClient {
    private final AthenaClient client;
    private final Properties properties;
    private final ScheduledExecutorService executor;

    public AwsAthenaClient(final ScheduledExecutorService executor, final Properties properties) {
        this.executor = executor;
        this.properties = properties;
        this.client = AthenaClient.builder()
                .region(properties.region)
                .build();
    }

    public CompletableFuture<GetQueryResultsIterable> query(final String query, String... args) {
        final var execution = startExecution(query, args);
        final var results = new CompletableFuture<GetQueryResultsIterable>();
        pollForCompletion(execution.queryExecutionId(), results);
        return results;
    }

    public StartQueryExecutionResponse startExecution(final String query, String... args) {
        final var request = StartQueryExecutionRequest.builder()
                .queryString(query)
                .executionParameters(args)
                .queryExecutionContext(QueryExecutionContext.builder()
                        .database(properties.database)
                        .build())
                .resultConfiguration(ResultConfiguration.builder()
                        .outputLocation(properties.outputLocation)
                        .build())
                .build();

        return client.startQueryExecution(request);
    }

    private void pollForCompletion(final String queryExecutionId, CompletableFuture<GetQueryResultsIterable> results) {
        final var checkFuture = executor.scheduleAtFixedRate(() -> {
            final var status = queryExecutionStatus(queryExecutionId);
            System.out.println("Query status: " + status.state());
            switch (status.state()) {
                case QUEUED:
                case RUNNING:
                    break;
                case SUCCEEDED:
                    results.complete(queryResults(queryExecutionId));
                    break;
                case FAILED:
                    results.completeExceptionally(OnlyDustException.internalServerError("Query failed: " + status.athenaError().toString()));
                    break;
                case CANCELLED:
                    results.completeExceptionally(new RuntimeException("Query cancelled"));
                    break;
            }
        }, 0, properties.pollingInterval, TimeUnit.SECONDS);

        results.whenComplete((result, thrown) -> {
            checkFuture.cancel(true);
        });
    }

    private QueryExecutionStatus queryExecutionStatus(final String queryExecutionId) {
        return client.getQueryExecution(GetQueryExecutionRequest.builder()
                .queryExecutionId(queryExecutionId)
                .build()).queryExecution().status();
    }

    private GetQueryResultsIterable queryResults(final String queryExecutionId) {
        return client.getQueryResultsPaginator(GetQueryResultsRequest.builder()
                .queryExecutionId(queryExecutionId)
                .maxResults(1)
                .build());
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Properties {
        Region region;
        String database;
        String outputLocation;
        Integer pollingInterval;
    }
}
