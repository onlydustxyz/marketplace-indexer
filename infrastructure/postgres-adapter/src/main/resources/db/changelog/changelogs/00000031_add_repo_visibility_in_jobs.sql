ALTER TABLE indexer.repo_indexing_jobs
    ADD is_public BOOLEAN NOT NULL DEFAULT TRUE;

UPDATE indexer.repo_indexing_jobs
SET is_public = r.visibility = 'PUBLIC'
FROM indexer_exp.github_repos r
WHERE indexer.repo_indexing_jobs.repo_id = r.id;

ALTER TABLE indexer.repo_indexing_jobs
    ALTER COLUMN is_public DROP DEFAULT;
