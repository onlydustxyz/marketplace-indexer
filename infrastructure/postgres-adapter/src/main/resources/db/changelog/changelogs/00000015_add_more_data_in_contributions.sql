ALTER TABLE indexer_exp.github_pull_requests
    ADD repo_html_url     TEXT,
    ADD author_login      TEXT,
    ADD author_html_url   TEXT,
    ADD author_avatar_url TEXT
;

ALTER TABLE indexer_exp.github_issues
    ADD repo_html_url     TEXT,
    ADD author_login      TEXT,
    ADD author_html_url   TEXT,
    ADD author_avatar_url TEXT
;

ALTER TABLE indexer_exp.github_code_reviews
    ADD repo_id           BIGINT references indexer_exp.github_repos (id),
    ADD repo_html_url     TEXT,
    ADD author_login      TEXT,
    ADD author_html_url   TEXT,
    ADD author_avatar_url TEXT
;

ALTER TABLE indexer_exp.contributions
    ADD repo_owner_login         TEXT,
    ADD repo_name                TEXT,
    ADD repo_html_url            TEXT,
    ADD github_author_id         BIGINT references indexer_exp.github_accounts (id),
    ADD github_author_login      TEXT,
    ADD github_author_html_url   TEXT,
    ADD github_author_avatar_url TEXT
;

UPDATE indexer_exp.github_pull_requests
SET repo_html_url = r.html_url
FROM indexer_exp.github_repos r
WHERE repo_id = r.id;

UPDATE indexer_exp.github_pull_requests
SET author_login      = a.login,
    author_html_url   = a.html_url,
    author_avatar_url = a.avatar_url
FROM indexer_exp.github_accounts a
WHERE author_id = a.id;

UPDATE indexer_exp.github_issues
SET repo_html_url = r.html_url
FROM indexer_exp.github_repos r
WHERE repo_id = r.id;

UPDATE indexer_exp.github_issues
SET author_login      = a.login,
    author_html_url   = a.html_url,
    author_avatar_url = a.avatar_url
FROM indexer_exp.github_accounts a
WHERE author_id = a.id;

UPDATE indexer_exp.github_code_reviews
SET repo_id       = pr.repo_id,
    repo_html_url = pr.html_url
FROM indexer_exp.github_pull_requests pr
WHERE pull_request_id = pr.id;

UPDATE indexer_exp.github_code_reviews
SET author_login      = a.login,
    author_html_url   = a.html_url,
    author_avatar_url = a.avatar_url
FROM indexer_exp.github_accounts a
WHERE author_id = a.id;

UPDATE indexer_exp.contributions
SET repo_owner_login         = pr.repo_owner_login,
    repo_name                = pr.repo_name,
    repo_html_url            = pr.repo_html_url,
    github_author_id         = pr.author_id,
    github_author_login      = pr.author_login,
    github_author_html_url   = pr.author_html_url,
    github_author_avatar_url = pr.author_avatar_url
FROM indexer_exp.github_pull_requests pr
WHERE contributions.pull_request_id = pr.id;

UPDATE indexer_exp.contributions
SET repo_owner_login         = i.repo_owner_login,
    repo_name                = i.repo_name,
    repo_html_url            = i.repo_html_url,
    github_author_id         = i.author_id,
    github_author_login      = i.author_login,
    github_author_html_url   = i.author_html_url,
    github_author_avatar_url = i.author_avatar_url
FROM indexer_exp.github_issues i
WHERE contributions.issue_id = i.id;

UPDATE indexer_exp.contributions
SET repo_owner_login         = cr.repo_owner_login,
    repo_name                = cr.repo_name,
    repo_html_url            = cr.repo_html_url,
    github_author_id         = cr.author_id,
    github_author_login      = cr.author_login,
    github_author_html_url   = cr.author_html_url,
    github_author_avatar_url = cr.author_avatar_url
FROM indexer_exp.github_code_reviews cr
WHERE contributions.code_review_id = cr.id;

ALTER TABLE indexer_exp.github_pull_requests
    ALTER COLUMN repo_html_url SET NOT NULL,
    ALTER COLUMN author_login SET NOT NULL,
    ALTER COLUMN author_html_url SET NOT NULL,
    ALTER COLUMN author_avatar_url SET NOT NULL
;

ALTER TABLE indexer_exp.github_issues
    ALTER COLUMN repo_html_url SET NOT NULL,
    ALTER COLUMN author_login SET NOT NULL,
    ALTER COLUMN author_html_url SET NOT NULL,
    ALTER COLUMN author_avatar_url SET NOT NULL
;

ALTER TABLE indexer_exp.github_code_reviews
    ALTER COLUMN repo_id SET NOT NULL,
    ALTER COLUMN repo_html_url SET NOT NULL,
    ALTER COLUMN author_login SET NOT NULL,
    ALTER COLUMN author_html_url SET NOT NULL,
    ALTER COLUMN author_avatar_url SET NOT NULL
;

ALTER TABLE indexer_exp.contributions
    ALTER COLUMN repo_owner_login SET NOT NULL,
    ALTER COLUMN repo_name SET NOT NULL,
    ALTER COLUMN repo_html_url SET NOT NULL,
    ALTER COLUMN github_author_id SET NOT NULL,
    ALTER COLUMN github_author_login SET NOT NULL,
    ALTER COLUMN github_author_html_url SET NOT NULL,
    ALTER COLUMN github_author_avatar_url SET NOT NULL
;