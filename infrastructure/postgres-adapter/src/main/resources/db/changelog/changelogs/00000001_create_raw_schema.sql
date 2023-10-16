drop schema indexer_clean;
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
    repo_id    bigint    NOT NULL references indexer_raw.repos (id),
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
    id              bigint primary key,
    pull_request_id bigint    NOT NULL references indexer_raw.pull_requests (id),
    reviewer_id     bigint    NOT NULL references indexer_raw.users (id),
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
    issue_id        bigint references indexer_raw.issues (id),
    created_at      timestamp not null,
    updated_at      timestamp not null,
    PRIMARY KEY (pull_request_id, issue_id)
);


create unique index pull_request_closing_issues_issue_id_pull_request_id on indexer_raw.pull_request_closing_issues (issue_id, pull_request_id);
