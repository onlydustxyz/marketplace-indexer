create table indexer.repo_indexing_job_triggers
(
    repo_id         BIGINT PRIMARY KEY,
    installation_id BIGINT
);

create table indexer.user_indexing_job_triggers
(
    repo_id BIGINT PRIMARY KEY
);
