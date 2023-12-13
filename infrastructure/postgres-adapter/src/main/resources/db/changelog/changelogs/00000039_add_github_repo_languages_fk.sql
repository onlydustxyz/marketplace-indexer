ALTER TABLE indexer_exp.github_repo_languages
    ADD FOREIGN KEY (repo_id) REFERENCES indexer_exp.github_repos (id);

