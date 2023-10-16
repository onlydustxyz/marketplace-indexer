create table indexer_raw.installation_events
(
    id         serial primary key,
    data       jsonb,
    created_at timestamp NOT NULL
);
