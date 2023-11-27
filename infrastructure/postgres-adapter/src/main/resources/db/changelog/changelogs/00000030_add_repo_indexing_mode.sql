ALTER TABLE indexer.repo_indexing_jobs
    ADD full_indexing BOOLEAN NOT NULL DEFAULT TRUE;

ALTER TABLE indexer.repo_indexing_jobs
    ALTER COLUMN full_indexing DROP DEFAULT;
