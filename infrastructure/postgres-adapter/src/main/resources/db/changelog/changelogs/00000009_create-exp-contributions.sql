CREATE TYPE indexer_exp.github_issue_status AS ENUM ('OPEN', 'COMPLETED', 'CANCELLED');

CREATE TABLE indexer_exp.github_issues
(
    id             BIGINT PRIMARY KEY,
    repo_id        BIGINT                          NOT NULL references indexer_exp.github_repos (id),
    number         BIGINT                          NOT NULL,
    title          TEXT                            NOT NULL,
    status         indexer_exp.github_issue_status NOT NULL,
    created_at     TIMESTAMP                       NOT NULL,
    closed_at      TIMESTAMP,
    author_id      BIGINT                          NOT NULL references indexer_exp.github_accounts (id),
    html_url       TEXT                            NOT NULL,
    comments_count INTEGER                         NOT NULL
);

CREATE TABLE indexer_exp.github_issues_assignees
(
    issue_id BIGINT references indexer_exp.github_issues (id),
    user_id  BIGINT references indexer_exp.github_accounts (id),
    PRIMARY KEY (issue_id, user_id)
);

CREATE TYPE indexer_exp.github_pull_request_status AS ENUM ('OPEN', 'CLOSED', 'MERGED');


CREATE TABLE indexer_exp.github_pull_requests
(
    id             BIGINT PRIMARY KEY,
    repo_id        BIGINT                                 NOT NULL references indexer_exp.github_repos (id),
    number         BIGINT                                 NOT NULL,
    title          TEXT                                   NOT NULL,
    status         indexer_exp.github_pull_request_status NOT NULL,
    created_at     TIMESTAMP                              NOT NULL,
    closed_at      TIMESTAMP,
    merged_at      TIMESTAMP,
    author_id      BIGINT                                 NOT NULL references indexer_exp.github_accounts (id),
    html_url       TEXT                                   NOT NULL,
    comments_count INTEGER                                NOT NULL
);

CREATE TABLE indexer_exp.github_pull_requests_closing_issues
(
    pull_request_id BIGINT references indexer_exp.github_pull_requests (id),
    issue_id        BIGINT references indexer_exp.github_issues (id),
    PRIMARY KEY (pull_request_id, issue_id)
);

CREATE INDEX github_pull_requests_closing_issues_issue_id_idx
    ON indexer_exp.github_pull_requests_closing_issues (issue_id, pull_request_id);

CREATE TYPE indexer_exp.github_code_review_state
AS ENUM ('PENDING', 'COMMENTED', 'APPROVED', 'CHANGES_REQUESTED', 'DISMISSED');

CREATE TABLE indexer_exp.github_code_reviews
(
    id              TEXT PRIMARY KEY,
    pull_request_id BIGINT                               NOT NULL references indexer_exp.github_pull_requests (id),
    author_id       BIGINT                               NOT NULL references indexer_exp.github_accounts (id),
    state           indexer_exp.github_code_review_state NOT NULL,
    requested_at    TIMESTAMP                            NOT NULL,
    submitted_at    TIMESTAMP
);

CREATE TYPE indexer_exp.contribution_type AS ENUM ('PULL_REQUEST', 'ISSUE', 'CODE_REVIEW');
CREATE TYPE indexer_exp.contribution_status AS ENUM ('IN_PROGRESS', 'COMPLETED', 'CANCELLED');

CREATE TABLE indexer_exp.contributions
(
    id              TEXT PRIMARY KEY,
    repo_id         BIGINT                          NOT NULL references indexer_exp.github_repos (id),
    contributor_id  BIGINT                          NOT NULL references indexer_exp.github_accounts (id),
    type            indexer_exp.contribution_type   NOT NULL,
    status          indexer_exp.contribution_status NOT NULL,
    pull_request_id BIGINT references indexer_exp.github_pull_requests (id),
    issue_id        BIGINT references indexer_exp.github_issues (id),
    code_review_id  TEXT references indexer_exp.github_code_reviews (id),
    created_at      TIMESTAMP                       NOT NULL,
    completed_at    TIMESTAMP,
    UNIQUE (contributor_id, repo_id, pull_request_id, issue_id),
    CHECK ((type = 'PULL_REQUEST' AND pull_request_id IS NOT NULL AND issue_id IS NULL AND code_review_id IS NULL)
        OR (type = 'ISSUE' AND pull_request_id IS NULL AND issue_id IS NOT NULL AND code_review_id IS NULL)
        OR (type = 'CODE_REVIEW' AND pull_request_id IS NULL AND issue_id IS NULL AND code_review_id IS NOT NULL))
);