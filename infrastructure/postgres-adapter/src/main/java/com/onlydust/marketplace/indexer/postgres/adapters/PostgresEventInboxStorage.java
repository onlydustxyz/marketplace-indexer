package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.models.raw.github_app_events.RawGithubAppEvent;
import com.onlydust.marketplace.indexer.domain.ports.out.EventInboxStorage;
import com.onlydust.marketplace.indexer.postgres.entities.EventsInboxEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.raw.EventsInboxEntityRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import java.util.Optional;

import static com.onlydust.marketplace.indexer.domain.exception.OnlyDustException.notFound;

@AllArgsConstructor
public class PostgresEventInboxStorage implements EventInboxStorage {
    private final EventsInboxEntityRepository eventsInboxEntityRepository;

    @Override
    public void save(RawGithubAppEvent event) {
        eventsInboxEntityRepository.merge(new EventsInboxEntity(event.type(), event.payload()));
    }

    @Override
    public Optional<RawGithubAppEvent> peek(String... types) {
        return eventsInboxEntityRepository.findFirstToProcess(types)
                .map(entity -> new RawGithubAppEvent(entity.id(), entity.type(), entity.payload()));
    }

    @Override
    @Transactional
    public void ack(Long id) {
        event(id).processed();
    }

    @Override
    @Transactional
    public void nack(Long id, String reason) {
        event(id).failed(reason);
    }

    @Override
    @Transactional
    public void ignore(Long id, String reason) {
        event(id).ignored(reason);
    }

    private EventsInboxEntity event(Long id) {
        return eventsInboxEntityRepository.findById(id).orElseThrow(() -> notFound("Event %d not found".formatted(id)));
    }
}
