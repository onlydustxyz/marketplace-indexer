package com.onlydust.marketplace.indexer.infrastructure.github_archives;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.onlydust.marketplace.indexer.infrastructure.github_archives.adapters.GithubArchivesPublicEventRawStorageReaderAdapter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.Objects;

class GithubArchivesClientTest {
    final ObjectMapper objectMapper = JsonMapper.builder()
            .findAndAddModules()
            .build()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    @SneakyThrows
    @Test
    void query() {
        final var properties = new GithubArchivesClient.Properties(
                "od-develop",
                Files.readString(Path.of(Objects.requireNonNull(getClass().getResource("/od-develop-de39954cac43.json")).getPath()))
        );

        final var client = new GithubArchivesClient(properties);

//        client.query("""
//                 SELECT  id                    as id,
//                            type                  as type,
//                            to_json_string(actor) as actor,
//                            to_json_string(repo)  as repo,
//                            to_json_string(org)   as org,
//                            created_at            as created_at,
//                            payload               as payload
//                    FROM `githubarchive.month.202201`
//                    WHERE actor.id = @actor_id
//                    ORDER BY created_at ASC
//                """, Map.of("actor_id", QueryParameterValue.int64(43467246))).streamAll().forEach(System.out::println);

        final var adapter = new GithubArchivesPublicEventRawStorageReaderAdapter(client);

        adapter.userPublicEvents(43467246L, ZonedDateTime.parse("2019-02-10T00:00Z"))
                .forEach(e -> {
                    try {
                        System.out.println(objectMapper.writeValueAsString(e));
                    } catch (JsonProcessingException ex) {
                        throw new RuntimeException(ex);
                    }
                });
    }
}