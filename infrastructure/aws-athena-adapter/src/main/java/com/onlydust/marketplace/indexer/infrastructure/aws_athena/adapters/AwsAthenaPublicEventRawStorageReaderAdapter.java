package com.onlydust.marketplace.indexer.infrastructure.aws_athena.adapters;

import com.onlydust.marketplace.indexer.domain.models.raw.public_events.RawPublicEvent;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.PublicEventRawStorageReader;
import com.onlydust.marketplace.indexer.infrastructure.aws_athena.AwsAthenaClient;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

@AllArgsConstructor
public class AwsAthenaPublicEventRawStorageReaderAdapter implements PublicEventRawStorageReader {
    private final static DateTimeFormatter ATHENA_TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private final AwsAthenaClient client;

    @SneakyThrows
    @Override
    public Stream<RawPublicEvent> userPublicEvents(Long userId, ZonedDateTime since) {
        return client.query("""
                        SELECT event from gha_data_per_actor
                        where actor_id = ? and actor_pk = ? and created_at >= ?
                        order by created_at
                        """, userId.toString(), Long.toString(userId % 100), "timestamp '%s'".formatted(since.format(ATHENA_TIMESTAMP_FORMATTER)))
                .get()
                .stream().flatMap(r -> r.resultSet().rows().stream())
                .skip(1) // First row is the header
                .map(r -> r.data().get(0).varCharValue())
                .map(RawPublicEvent::fromJson);
    }

    @SneakyThrows
    @Override
    public Stream<RawPublicEvent> allPublicEvents(ZonedDateTime timestamp) {
        return client.query("""
                                SELECT event from gha_data_per_date
                                where year = ? and month = ? and day = ? and hour = ?
                                order by created_at
                                """, String.valueOf(timestamp.getYear()),
                        String.valueOf(timestamp.getMonthValue()),
                        String.valueOf(timestamp.getDayOfMonth()),
                        String.valueOf(timestamp.getHour()))
                .get()
                .stream().flatMap(r -> r.resultSet().rows().stream())
                .skip(1) // First row is the header
                .map(r -> r.data().get(0).varCharValue())
                .map(RawPublicEvent::fromJson);
    }
}
