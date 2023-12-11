DROP INDEX indexer_exp.github_repos_owner_login_name_idx;

CREATE INDEX github_repos_owner_login_name_idx
    ON indexer_exp.github_repos (owner_login, name);
