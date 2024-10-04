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

create index indexer_raw_public_events_actor_id_created_at_idx
    on indexer_raw.public_events (actor_id, created_at);

create table user_public_events_indexing_jobs
(
    user_id              bigint primary key,
    status               indexer.job_status       default 'PENDING'::indexer.job_status not null,
    started_at           timestamp with time zone,
    finished_at          timestamp with time zone,
    last_event_timestamp timestamp with time zone,
    tech_created_at      timestamp with time zone default now()                         not null,
    tech_updated_at      timestamp with time zone default now()                         not null
);

create trigger user_stats_indexing_jobs_set_tech_updated_at
    before update
    on user_public_events_indexing_jobs
    for each row
execute function set_tech_updated_at();

alter table indexer_raw.issues
    drop constraint issues_repo_id_fkey;

alter table indexer_raw.pull_request_commits
    drop constraint pull_request_commits_pull_request_id_fkey;

alter table indexer_raw.pull_request_reviews
    drop constraint pull_request_reviews_pull_request_id_fkey;

alter table indexer_raw.pull_requests
    drop constraint pull_requests_repo_id_fkey;

alter table indexer_raw.repo_languages
    drop constraint repo_languages_repo_id_fkey;

alter table indexer_raw.user_social_accounts
    drop constraint user_social_accounts_user_id_fkey;

