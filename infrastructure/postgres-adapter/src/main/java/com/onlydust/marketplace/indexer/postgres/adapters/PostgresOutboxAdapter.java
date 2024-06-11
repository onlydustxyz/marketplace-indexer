package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.postgres.OutboxRepository;
import com.onlydust.marketplace.indexer.postgres.entities.EventEntity;
import lombok.AllArgsConstructor;
import onlydust.com.marketplace.kernel.model.Event;
import onlydust.com.marketplace.kernel.port.output.OutboxPort;

import java.util.Optional;

import static com.onlydust.marketplace.indexer.domain.exception.OnlyDustException.internalServerError;

@AllArgsConstructor
public class PostgresOutboxAdapter<E extends EventEntity> implements OutboxPort {

    private final OutboxRepository<E> outboxRepository;

    @Override
    public void push(Event event) {
        outboxRepository.saveEvent(event);
    }

    @Override
    public Optional<IdentifiableEvent> peek() {
        return outboxRepository.findNextToProcess().map(EventEntity::toIdentifiableEvent);
    }

    @Override
    public void ack(Long eventId) {
        final var entity = outboxRepository.findById(eventId)
                .orElseThrow(() -> internalServerError("Event %d not found".formatted(eventId)));

        entity.setStatus(EventEntity.Status.PROCESSED);
        entity.setError(null);
        outboxRepository.saveAndFlush(entity);
    }

    @Override
    public void nack(Long eventId, String message) {
        final var entity = outboxRepository.findById(eventId)
                .orElseThrow(() -> internalServerError("Event %d not found".formatted(eventId)));

        entity.setStatus(EventEntity.Status.FAILED);
        entity.setError(message);
        outboxRepository.saveAndFlush(entity);
    }

    @Override
    public void skip(Long eventId, String message) {
        final var entity = outboxRepository.findById(eventId)
                .orElseThrow(() -> internalServerError("Event %d not found".formatted(eventId)));

        entity.setStatus(EventEntity.Status.SKIPPED);
        entity.setError(message);
        outboxRepository.saveAndFlush(entity);
    }
}
