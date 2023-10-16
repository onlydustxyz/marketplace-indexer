create schema indexer_exp;

create table indexer_exp.github_accounts
(
    id              BIGINT PRIMARY KEY,
    login           TEXT NOT NULL,
    type            TEXT NOT NULL,
    html_url        TEXT NOT NULL,
    avatar_url      TEXT,
    installation_id BIGINT
);


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
