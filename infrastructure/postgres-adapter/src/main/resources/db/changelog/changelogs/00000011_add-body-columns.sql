ALTER TABLE indexer_exp.github_issues
    ADD COLUMN body text;

ALTER TABLE indexer_exp.github_pull_requests
    ADD COLUMN body text;