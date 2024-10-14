package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.postgres.OutboxRepository;
import com.onlydust.marketplace.indexer.postgres.entities.EventEntity;
import jakarta.transaction.Transactional;
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
    @Transactional
    public void ack(Long eventId) {
        event(eventId)
                .status(EventEntity.Status.PROCESSED)
                .error(null);
    }

    @Override
    @Transactional
    public void nack(Long eventId, String message) {
        event(eventId)
                .status(EventEntity.Status.FAILED)
                .error(message);
    }

    @Override
    @Transactional
    public void skip(Long eventId, String message) {
        event(eventId)
                .status(EventEntity.Status.SKIPPED)
                .error(message);
    }

    private E event(Long eventId) {
        return outboxRepository.findById(eventId)
                .orElseThrow(() -> internalServerError("Event %d not found".formatted(eventId)));
    }
}
