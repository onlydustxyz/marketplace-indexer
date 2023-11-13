CREATE TYPE indexer.job_status AS ENUM ('PENDING', 'RUNNING', 'SUCCESS', 'FAILED');

ALTER TABLE indexer.repo_indexing_job_triggers
    ADD status      indexer.job_status NOT NULL DEFAULT 'PENDING',
    ADD started_at  TIMESTAMP,
    ADD finished_at TIMESTAMP
;

ALTER TABLE indexer.repo_indexing_job_triggers
    RENAME TO repo_indexing_jobs;
