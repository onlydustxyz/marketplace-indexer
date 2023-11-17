ALTER TABLE indexer_exp.github_repos
    ALTER COLUMN updated_at DROP NOT NULL,
    ALTER COLUMN forks_count DROP NOT NULL,
    ALTER COLUMN stars_count DROP NOT NULL,
    ALTER COLUMN has_issues DROP NOT NULL,
    ALTER COLUMN has_issues DROP DEFAULT
;