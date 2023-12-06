package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.exception.OnlyDustException;
import com.onlydust.marketplace.indexer.domain.models.raw.RawEvent;
import com.onlydust.marketplace.indexer.domain.ports.out.EventInboxStorage;
import com.onlydust.marketplace.indexer.postgres.entities.EventsInboxEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.raw.EventsInboxEntityRepository;
import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@AllArgsConstructor
public class PostgresEventInboxStorage implements EventInboxStorage {
    private final EventsInboxEntityRepository eventsInboxEntityRepository;

    @Override
    public void save(RawEvent event) {
        eventsInboxEntityRepository.save(new EventsInboxEntity(event.getType(), event.getPayload()));
    }

    @Override
    public Optional<RawEvent> peek(String... types) {
        return eventsInboxEntityRepository.findFirstToProcess(Arrays.stream(types).toList())
                .map(entity -> new RawEvent(entity.getId(), entity.getType(), entity.getPayload()));
    }

    @Override
    public void ack(Long id) {
        final var event = eventsInboxEntityRepository.findById(id).orElseThrow(() -> OnlyDustException.notFound("Event %d not found".formatted(id)));
        eventsInboxEntityRepository.save(event.processed());
    }

    @Override
    public void nack(Long id, String reason) {
        final var event = eventsInboxEntityRepository.findById(id).orElseThrow(() -> OnlyDustException.notFound("Event %d not found".formatted(id)));
        eventsInboxEntityRepository.save(event.failed(reason));
    }

    @Override
    public void ignore(Long id, String reason) {
        final var event = eventsInboxEntityRepository.findById(id).orElseThrow(() -> OnlyDustException.notFound("Event %d not found".formatted(id)));
        eventsInboxEntityRepository.save(event.ignored(reason));
    }
}
