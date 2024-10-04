package com.onlydust.marketplace.indexer.bootstrap.it.stubs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.onlydust.marketplace.indexer.domain.models.raw.public_events.RawPublicEvent;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.PublicEventRawStorageReader;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;

public class PublicEventRawStorageReaderStub implements PublicEventRawStorageReader {
    private final ObjectMapper objectMapper = JsonMapper.builder().findAndAddModules().build().registerModule(new JavaTimeModule());
    private final List<RawPublicEvent> events = new ArrayList<>();

    @SneakyThrows
    private RawPublicEvent[] fromPath(String path) {
        final var event = Files.readString(Paths.get(requireNonNull(this.getClass().getResource(path)).toURI()));
        return objectMapper.readValue(event, RawPublicEvent[].class);
    }

    public void add(String... paths) {
        events.addAll(Stream.of(paths)
                .map(this::fromPath)
                .flatMap(Stream::of)
                .toList());
    }

    @Override
    public Stream<RawPublicEvent> userPublicEvents(Long userId, ZonedDateTime since) {
        return events.stream()
                .filter(e -> e.actor().getId().equals(userId) && e.createdAt().isAfter(since))
                .sorted(comparing(RawPublicEvent::createdAt));
    }
}
