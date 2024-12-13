package com.onlydust.marketplace.indexer.infrastructure.aws_athena.adapters;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import com.onlydust.marketplace.indexer.domain.models.raw.public_events.RawPublicEvent;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.PublicEventRawStorageReader;
import com.onlydust.marketplace.indexer.infrastructure.aws_athena.AwsAthenaClient;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class AwsAthenaPublicEventRawStorageReaderAdapter implements PublicEventRawStorageReader {
    private final static DateTimeFormatter ATHENA_TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private final AwsAthenaClient client;
    private final AwsAthenaClient.Properties properties;

    @SneakyThrows
    @Override
    public Stream<RawPublicEvent> userPublicEvents(Long userId, ZonedDateTime since) {
        return client.query("""
                SELECT event from gha_data_per_actor
                where actor_id = ? and actor_pk = ? and created_at >= ?
                order by created_at
                """, userId.toString(), Long.toString(userId % 100), 
                "timestamp '%s'".formatted(since.format(ATHENA_TIMESTAMP_FORMATTER)))
                .get()
                .stream()
                .flatMap(r -> r.resultSet().rows().stream())
                .skip(1) // First row is the header
                .map(r -> r.data().get(0).varCharValue())
                .map(RawPublicEvent::fromJson);
    }

    @SneakyThrows
    @Override
    public Stream<RawPublicEvent> allPublicEvents(ZonedDateTime timestamp, List<Long> userIds) {
        final var batches = userIds.stream()
                .collect(groupingBy(id -> userIds.indexOf(id) / properties.getBatchSize()))
                .values().stream().toList();
                
        LOGGER.info("Processing {} user IDs in {} batches", userIds.size(), batches.size());

        final var queries = batches.stream()
                .map(batch -> createBatchQuery(batch, timestamp))
                .toList();

        final var futures = client.batchQuery(queries);

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
                .thenApply(v -> futures.stream().flatMap(future -> future.join().stream()))
                .join()
                .flatMap(r -> r.resultSet().rows().stream())
                .skip(1) // First row is the header
                .map(r -> r.data().get(0).varCharValue())
                .map(RawPublicEvent::fromJson);
    }

    private AwsAthenaClient.BatchQuery createBatchQuery(List<Long> batch, ZonedDateTime timestamp) {
        final var userIdList = batch.stream()
                .map(String::valueOf)
                .collect(joining(","));
        
        LOGGER.debug("Creating query for batch of {} users", batch.size());
        return new AwsAthenaClient.BatchQuery(
                """
                SELECT event from gha_data_per_date
                where year = ? and month = ? and day = ? and hour = ?
                and actor_id IN (%s)
                order by actor_id, created_at
                """.formatted(userIdList),
                new String[]{
                    String.valueOf(timestamp.getYear()),
                    String.valueOf(timestamp.getMonthValue()),
                    String.valueOf(timestamp.getDayOfMonth()),
                    String.valueOf(timestamp.getHour())
                }
        );
    }

}
