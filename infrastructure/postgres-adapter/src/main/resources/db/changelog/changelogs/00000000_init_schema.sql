-- ----------------------------------------------
-- SCHEMA INDEXER_RAW
-- ----------------------------------------------
create schema indexer_raw;

-- repos
create table
    indexer_raw.repos
(
    id         bigint primary key,
    owner      text      not null,
    name       text      not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    data       JSONB     NOT NULL
);


create index repo_owner_name on indexer_raw.repos (owner, name);


create table
    indexer_raw.repo_languages
(
    repo_id    bigint primary key references indexer_raw.repos (id),
    created_at timestamp not null,
    updated_at timestamp not null,
    data       JSONB     NOT NULL
);


create table
    indexer_raw.repo_check_runs
(
    repo_id    bigint    NOT NULL,
    sha        TEXT      NOT NULL,
    created_at timestamp not null,
    updated_at timestamp not null,
    data       JSONB     NOT NULL,
    PRIMARY KEY (repo_id, sha)
);


-- users
create table
    indexer_raw.users
(
    id         bigint primary key,
    login      TEXT      not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    data       JSONB     NOT NULL
);


create index users_login on indexer_raw.users (login);


create table
    indexer_raw.user_social_accounts
(
    user_id    bigint primary key references indexer_raw.users (id),
    created_at timestamp not null,
    updated_at timestamp not null,
    data       JSONB     NOT NULL
);


-- issueIdNumbers
create table
    indexer_raw.issues
(
    id         bigint primary key,
    repo_id    bigint    not null references indexer_raw.repos (id),
    number     bigint    not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    data       JSONB     NOT NULL
);


create index issues_repo_id_number on indexer_raw.issues (repo_id, number);


-- pull requests
create table
    indexer_raw.pull_requests
(
    id         bigint primary key,
    repo_id    bigint    not null references indexer_raw.repos (id),
    number     bigint    not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    data       JSONB     NOT NULL
);


create index pull_requests_repo_id_number on indexer_raw.pull_requests (repo_id, number);


create table
    indexer_raw.pull_request_reviews
(
    pull_request_id bigint primary key references indexer_raw.pull_requests (id),
    created_at      timestamp not null,
    updated_at      timestamp not null,
    data            JSONB     NOT NULL
);


create table
    indexer_raw.pull_request_commits
(
    pull_request_id bigint PRIMARY KEY references indexer_raw.pull_requests (id),
    created_at      timestamp not null,
    updated_at      timestamp not null,
    data            JSONB     NOT NULL
);


create table
    indexer_raw.pull_request_closing_issues
(
    pull_request_id bigint references indexer_raw.pull_requests (id),
    issue_id        bigint references indexer_raw.issues (id) DEFERRABLE INITIALLY DEFERRED,
    created_at      timestamp not null,
    updated_at      timestamp not null,
    PRIMARY KEY (pull_request_id, issue_id)
);


create unique index pull_request_closing_issues_issue_id_pull_request_id on indexer_raw.pull_request_closing_issues (issue_id, pull_request_id);

-- Installation events
create table indexer_raw.installation_events
(
    id         serial primary key,
    data       jsonb,
    created_at timestamp NOT NULL
);

-- ----------------------------------------------
-- SCHEMA INDEXER_EXP
-- ----------------------------------------------
create schema indexer_exp;

-- Accounts
CREATE TYPE indexer_exp.github_account_type AS ENUM ('USER', 'ORGANIZATION');

create table indexer_exp.github_accounts
(
    id              BIGINT PRIMARY KEY,
    login           TEXT                            NOT NULL,
    type            indexer_exp.github_account_type NOT NULL,
    html_url        TEXT                            NOT NULL,
    avatar_url      TEXT,
    installation_id BIGINT
);

-- Repos
create table indexer_exp.github_repos
(
    id          BIGINT PRIMARY KEY,
    owner_id    BIGINT    NOT NULL REFERENCES indexer_exp.github_accounts (id),
    name        TEXT      NOT NULL,
    html_url    TEXT      NOT NULL,
    updated_at  TIMESTAMP NOT NULL,
    description TEXT,
    stars_count BIGINT    NOT NULL,
    forks_count BIGINT    NOT NULL
);

-- Issues
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
    body           TEXT,
    comments_count INTEGER                         NOT NULL
);

CREATE TABLE indexer_exp.github_issues_assignees
(
    issue_id BIGINT references indexer_exp.github_issues (id),
    user_id  BIGINT references indexer_exp.github_accounts (id),
    PRIMARY KEY (issue_id, user_id)
);

-- Pull requests
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
    body           TEXT,
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

-- Code reviews
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

-- Contributions
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

-- ----------------------------------------------
-- SCHEMA INDEXER
-- ----------------------------------------------
create table indexer.repo_indexing_job_triggers
(
    repo_id         BIGINT PRIMARY KEY,
    installation_id BIGINT
);

create table indexer.user_indexing_job_triggers
(
    user_id BIGINT PRIMARY KEY
);
