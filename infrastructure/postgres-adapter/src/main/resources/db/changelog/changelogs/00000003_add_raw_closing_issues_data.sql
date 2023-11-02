DROP TABLE indexer_raw.pull_request_closing_issues;

CREATE TABLE indexer_raw.pull_request_closing_issues
(
    repo_owner          TEXT      NOT NULL,
    repo_name           TEXT      NOT NULL,
    pull_request_number BIGINT    NOT NULL,
    created_at          timestamp not null,
    updated_at          timestamp not null,
    data                JSONB     NOT NULL,
    PRIMARY KEY (repo_owner, repo_name, pull_request_number)
);
