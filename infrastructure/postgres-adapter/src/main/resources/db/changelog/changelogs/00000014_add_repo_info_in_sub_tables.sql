ALTER TABLE indexer_exp.github_pull_requests
    ADD repo_owner_login TEXT,
    ADD repo_name        TEXT
;

ALTER TABLE indexer_exp.github_issues
    ADD repo_owner_login TEXT,
    ADD repo_name        TEXT
;

ALTER TABLE indexer_exp.github_code_reviews
    ADD number           BIGINT,
    ADD title            TEXT,
    ADD html_url         TEXT,
    ADD body             TEXT,
    ADD comments_count   INTEGER,
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

UPDATE indexer_exp.github_code_reviews
SET number           = pr.number,
    title            = pr.title,
    html_url         = pr.html_url,
    body             = pr.body,
    comments_count   = pr.comments_count,
    repo_owner_login = pr.repo_owner_login,
    repo_name        = pr.repo_name
FROM indexer_exp.github_pull_requests pr
WHERE pull_request_id = pr.id;

ALTER TABLE indexer_exp.github_pull_requests
    ALTER COLUMN repo_owner_login SET NOT NULL,
    ALTER COLUMN repo_name SET NOT NULL;

ALTER TABLE indexer_exp.github_issues
    ALTER COLUMN repo_owner_login SET NOT NULL,
    ALTER COLUMN repo_name SET NOT NULL;

ALTER TABLE indexer_exp.github_issues
    ALTER COLUMN number SET NOT NULL,
    ALTER COLUMN title SET NOT NULL,
    ALTER COLUMN html_url SET NOT NULL,
    ALTER COLUMN comments_count SET NOT NULL,
    ALTER COLUMN repo_owner_login SET NOT NULL,
    ALTER COLUMN repo_name SET NOT NULL;