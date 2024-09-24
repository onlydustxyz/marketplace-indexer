create table indexer_raw.public_events
(
    id              bigint primary key,
    actor_id        bigint                   not null,
    type            text                     not null,
    created_at      timestamp with time zone not null,
    actor           jsonb                    not null,
    repo            jsonb                    not null,
    org             jsonb                    not null,
    payload         jsonb                    not null,
    tech_created_at timestamp with time zone not null,
    tech_updated_at timestamp with time zone not null
);

create trigger indexer_raw_public_events_set_tech_updated_at
    before update
    on indexer_raw.public_events
    for each row
execute function set_tech_updated_at();

create index indexer_raw_public_events_actor_id_idx
    on indexer_raw.public_events (actor_id);
