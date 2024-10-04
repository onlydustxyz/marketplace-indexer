package com.onlydust.marketplace.indexer.infrastructure.github_archives;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.onlydust.marketplace.indexer.domain.models.raw.public_events.RawPublicEvent;
import com.onlydust.marketplace.indexer.infrastructure.github_archives.adapters.GithubArchivesPublicEventRawStorageReaderAdapter;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

class GithubArchivesClientTest {
    final ObjectMapper objectMapper = JsonMapper.builder()
            .findAndAddModules()
            .build()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    @SneakyThrows
//    @Test
    void query() {
        final var properties = new GithubArchivesClient.Properties(
                "smarthome-a7b57",
                Files.readString(Path.of(Objects.requireNonNull(getClass().getResource("/smarthome-a7b57-ca2bf8238c04.json")).getPath()))
        );

        final var client = new GithubArchivesClient(properties);

        final var adapter = new GithubArchivesPublicEventRawStorageReaderAdapter(client);

        final var events = adapter.userPublicEvents(43467246L, ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS).minusDays(2)).toArray(RawPublicEvent[]::new);

        // print in json format
        System.out.println(objectMapper.writeValueAsString(events));
    }
}