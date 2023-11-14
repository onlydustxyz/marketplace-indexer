ALTER TABLE indexer.user_indexing_job_triggers
    ADD status      indexer.job_status NOT NULL DEFAULT 'PENDING',
    ADD started_at  TIMESTAMP,
    ADD finished_at TIMESTAMP
;

ALTER TABLE indexer.user_indexing_job_triggers
    RENAME TO user_indexing_jobs;
