ALTER TABLE indexer_exp.github_repos
    ADD owner_login TEXT;

UPDATE indexer_exp.github_repos
SET owner_login = a.login
FROM indexer_exp.github_accounts a
WHERE a.id = owner_id;

ALTER TABLE indexer_exp.github_repos
    ALTER COLUMN owner_login SET NOT NULL;

CREATE UNIQUE INDEX github_repos_owner_login_name_idx
    ON indexer_exp.github_repos (owner_login, name);