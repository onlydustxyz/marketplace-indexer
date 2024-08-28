create index if not exists events_inbox_status_type_tech_created_at_index
    on indexer_raw.events_inbox (status, type, tech_created_at);

create index if not exists api_events_status_id_index
    on indexer_outbox.api_events (status, id);

