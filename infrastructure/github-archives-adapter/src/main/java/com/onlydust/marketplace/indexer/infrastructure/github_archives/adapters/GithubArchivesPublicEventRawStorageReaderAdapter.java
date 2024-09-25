package com.onlydust.marketplace.indexer.infrastructure.github_archives.adapters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.QueryParameterValue;
import com.onlydust.marketplace.indexer.domain.models.raw.RawAccount;
import com.onlydust.marketplace.indexer.domain.models.raw.RawRepo;
import com.onlydust.marketplace.indexer.domain.models.raw.public_events.RawPublicEvent;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.PublicEventRawStorageReader;
import com.onlydust.marketplace.indexer.infrastructure.github_archives.GithubArchivesClient;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.stream.Stream;

import static com.onlydust.marketplace.indexer.domain.exception.OnlyDustException.internalServerError;

@AllArgsConstructor
public class GithubArchivesPublicEventRawStorageReaderAdapter implements PublicEventRawStorageReader {
    private final static ObjectMapper objectMapper = new ObjectMapper();
    private final static DateTimeFormatter YYYYMMDD = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final GithubArchivesClient client;

    private static RawPublicEvent from(FieldValueList row) {
        try {
            return new RawPublicEvent(
                    row.get("id").getLongValue(),
                    row.get("type").getStringValue(),
                    objectMapper.readValue(row.get("actor").getBytesValue(), RawAccount.class),
                    objectMapper.readValue(row.get("repo").getBytesValue(), RawRepo.class),
                    objectMapper.readValue(row.get("org").getBytesValue(), RawAccount.class),
                    row.get("createdAt").getTimestampInstant().atZone(ZoneOffset.UTC),
                    objectMapper.readTree(row.get("payload").getBytesValue())
            );
        } catch (IOException e) {
            throw internalServerError("Error while parsing BigQuery row", e);
        }
    }

    @Override
    public Stream<RawPublicEvent> userPublicEvents(Long userId, ZonedDateTime since) {
        final var from = since.truncatedTo(ChronoUnit.DAYS);
        final var to = ZonedDateTime.now().minusDays(1).truncatedTo(ChronoUnit.DAYS);

        if (from.isAfter(to))
            return Stream.empty();

        final var query = """
                    SELECT *
                    FROM `githubarchive.day.%s`
                    WHERE actor.id = @actor_id
                    ORDER BY created_at ASC
                """;

        final var params = Map.of("actor_id", QueryParameterValue.int64(userId));

        return Stream.iterate(since, date -> date.isBefore(to), date -> date.plusDays(1))
                .map(date -> date.format(YYYYMMDD))
                .flatMap(day -> client.query(query.formatted(day), params).streamAll())
                .map(GithubArchivesPublicEventRawStorageReaderAdapter::from);
    }
}
