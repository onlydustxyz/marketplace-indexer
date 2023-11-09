CREATE TABLE indexer_exp.authorized_github_repos
(
    repo_id         BIGINT NOT NULL REFERENCES indexer_exp.github_repos (id),
    installation_id BIGINT NOT NULL REFERENCES indexer_exp.github_app_installations (id),
    PRIMARY KEY (repo_id, installation_id)
);
