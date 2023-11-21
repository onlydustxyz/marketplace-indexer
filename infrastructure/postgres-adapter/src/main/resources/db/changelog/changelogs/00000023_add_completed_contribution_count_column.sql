ALTER TABLE indexer_exp.repos_contributors
    DROP COLUMN has_completed_contribution,
    ADD COLUMN completed_contribution_count INT NOT NULL DEFAULT 0,
    ADD COLUMN total_contribution_count     INT NOT NULL DEFAULT 0;

ALTER TABLE indexer_exp.repos_contributors
    ALTER COLUMN completed_contribution_count DROP DEFAULT,
    ALTER COLUMN total_contribution_count DROP DEFAULT;