create schema indexer;

create table public.github_repo_indexes
(
    repo_id                     BIGINT PRIMARY KEY,
    repo_indexer_state          JSONB,
    issues_indexer_state        JSONB,
    pull_requests_indexer_state JSONB,
    indexed_at                  TIMESTAMP
);

