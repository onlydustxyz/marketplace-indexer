create schema indexer_outbox;

create type indexer_outbox.outbox_event_status as enum ('PENDING', 'PROCESSED', 'FAILED', 'SKIPPED');

create table indexer_outbox.api_events
(
    id              BIGSERIAL PRIMARY KEY,
    payload         jsonb                              NOT NULL,
    status          indexer_outbox.outbox_event_status NOT NULL DEFAULT 'PENDING',
    error           TEXT,
    tech_created_at TIMESTAMP                          NOT NULL DEFAULT NOW(),
    tech_updated_at TIMESTAMP                          NOT NULL DEFAULT NOW()
);

create trigger indexer_outbox_api_events_set_tech_updated_at
    before update
    on indexer_outbox.api_events
    for each row
execute function set_tech_updated_at();
