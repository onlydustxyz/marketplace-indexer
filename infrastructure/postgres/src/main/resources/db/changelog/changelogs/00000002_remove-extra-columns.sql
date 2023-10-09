drop table
    indexer_raw.pull_request_reviews;

create table
    indexer_raw.pull_request_reviews
(
    pull_request_id bigint primary key references indexer_raw.pull_requests (id),
    created_at      timestamp not null,
    updated_at      timestamp not null,
    data            JSONB     NOT NULL
);

