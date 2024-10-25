package com.onlydust.marketplace.indexer.infrastructure.github_archives.adapters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.QueryParameterValue;
import com.onlydust.marketplace.indexer.domain.models.raw.public_events.RawPublicEvent;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.PublicEventRawStorageReader;
import com.onlydust.marketplace.indexer.infrastructure.github_archives.GithubArchivesClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.stream.Stream;

import static com.onlydust.marketplace.indexer.domain.exception.OnlyDustException.internalServerError;

@Slf4j
@AllArgsConstructor
public class GithubArchivesPublicEventRawStorageReaderAdapter implements PublicEventRawStorageReader {
    private final static ObjectMapper objectMapper = JsonMapper.builder().findAndAddModules().build();
    private final static DateTimeFormatter YYYYMMDD = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final GithubArchivesClient client;

    private static RawPublicEvent from(FieldValueList row) {
        try {
            return new RawPublicEvent(
                    row.get("id").getLongValue(),
                    row.get("type").getStringValue(),
                    objectMapper.readValue(row.get("actor").getStringValue(), RawPublicEvent.Account.class),
                    objectMapper.readValue(row.get("repo").getStringValue(), RawPublicEvent.Repo.class),
                    objectMapper.readValue(row.get("org").getStringValue(), RawPublicEvent.Account.class),
                    row.get("created_at").getTimestampInstant().atZone(ZoneOffset.UTC),
                    objectMapper.readTree(row.get("payload").getStringValue())
            );
        } catch (IOException e) {
            throw internalServerError("Error while parsing BigQuery row", e);
        }
    }

    @Override
    public Stream<RawPublicEvent> userPublicEvents(Long userId, ZonedDateTime since) {
        final var from = since.truncatedTo(ChronoUnit.DAYS);
        final var to = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS);

        if (from.isAfter(to)) {
            LOGGER.info("No events to index for user {}", userId);
            return Stream.empty();
        }

        final var query = """
                    SELECT  id                    as id,
                            type                  as type,
                            to_json_string(actor) as actor,
                            to_json_string(repo)  as repo,
                            to_json_string(org)   as org,
                            created_at            as created_at,
                            payload               as payload
                    FROM `githubarchive.day.%s`
                    WHERE actor.id = @actor_id
                    ORDER BY created_at ASC
                """;

        final var params = Map.of("actor_id", QueryParameterValue.int64(userId));

        return Stream.iterate(from, date -> date.isBefore(to), date -> date.plusDays(1))
                .peek(date -> LOGGER.info("Querying events for user {} on {}", userId, date))
                .map(date -> date.format(YYYYMMDD))
                .flatMap(day -> client.query(query.formatted(day), params).streamAll())
                .map(GithubArchivesPublicEventRawStorageReaderAdapter::from);
    }
}
