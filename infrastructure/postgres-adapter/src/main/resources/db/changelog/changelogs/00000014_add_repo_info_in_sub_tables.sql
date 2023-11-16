ALTER TABLE indexer_exp.github_pull_requests
    ADD repo_owner_login TEXT,
    ADD repo_name        TEXT
;

ALTER TABLE indexer_exp.github_issues
    ADD repo_owner_login TEXT,
    ADD repo_name        TEXT
;

UPDATE indexer_exp.github_pull_requests
SET repo_owner_login = r.owner_login,
    repo_name        = r.name
FROM indexer_exp.github_repos r
WHERE repo_id = r.id;

UPDATE indexer_exp.github_issues
SET repo_owner_login = r.owner_login,
    repo_name        = r.name
FROM indexer_exp.github_repos r
WHERE repo_id = r.id;

ALTER TABLE indexer_exp.github_pull_requests
    ALTER COLUMN repo_owner_login SET NOT NULL,
    ALTER COLUMN repo_name SET NOT NULL;

ALTER TABLE indexer_exp.github_issues
    ALTER COLUMN repo_owner_login SET NOT NULL,
    ALTER COLUMN repo_name SET NOT NULL;