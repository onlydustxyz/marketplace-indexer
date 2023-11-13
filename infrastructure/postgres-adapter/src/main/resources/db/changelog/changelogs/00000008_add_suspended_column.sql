ALTER TABLE indexer.repo_indexing_job_triggers
    ADD COLUMN suspended_at TIMESTAMP;

ALTER TABLE indexer_exp.github_app_installations
    ADD COLUMN suspended_at TIMESTAMP;
