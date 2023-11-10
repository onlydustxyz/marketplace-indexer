CREATE TABLE indexer_exp.repos_contributors
(
    repo_id        BIGINT NOT NULL REFERENCES indexer_exp.github_repos (id),
    contributor_id BIGINT NOT NULL REFERENCES indexer_exp.github_accounts (id),
    PRIMARY KEY (repo_id, contributor_id)
);
