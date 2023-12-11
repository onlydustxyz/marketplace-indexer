ALTER TABLE indexer_exp.github_repos
    ADD deleted_at timestamp;

ALTER TABLE indexer_raw.repos
    ADD deleted boolean NOT NULL DEFAULT false;
