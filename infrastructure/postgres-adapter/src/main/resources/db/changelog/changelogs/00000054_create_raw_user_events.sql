create table indexer_raw.user_events
(
    id              bigint primary key,
    user_id         bigint                   not null unique,
    timestamp       timestamp with time zone not null,
    data            jsonb                    not null,
    tech_created_at timestamp with time zone not null,
    tech_updated_at timestamp with time zone not null
);

create trigger indexer_raw_user_events_set_tech_updated_at
    before update
    on indexer_raw.user_events
    for each row
execute function set_tech_updated_at();

