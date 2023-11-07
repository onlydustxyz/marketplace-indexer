ALTER TABLE indexer_exp.contributions
    ADD tech_created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    ADD tech_updated_at timestamp not null DEFAULT NOW();

ALTER TABLE indexer_exp.github_accounts
    ADD tech_created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    ADD tech_updated_at timestamp not null DEFAULT NOW();

ALTER TABLE indexer_exp.github_app_installations
    ADD tech_created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    ADD tech_updated_at timestamp not null DEFAULT NOW();

ALTER TABLE indexer_exp.github_code_reviews
    ADD tech_created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    ADD tech_updated_at timestamp not null DEFAULT NOW();

ALTER TABLE indexer_exp.github_issues
    ADD tech_created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    ADD tech_updated_at timestamp not null DEFAULT NOW();

ALTER TABLE indexer_exp.github_pull_requests
    ADD tech_created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    ADD tech_updated_at timestamp not null DEFAULT NOW();

ALTER TABLE indexer_exp.github_repos
    ADD tech_created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    ADD tech_updated_at timestamp not null DEFAULT NOW();

ALTER TABLE indexer.repo_indexing_job_triggers
    ADD tech_created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    ADD tech_updated_at timestamp not null DEFAULT NOW();

ALTER TABLE indexer.user_indexing_job_triggers
    ADD tech_created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    ADD tech_updated_at timestamp not null DEFAULT NOW();

