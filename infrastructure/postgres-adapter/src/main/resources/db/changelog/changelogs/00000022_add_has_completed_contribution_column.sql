ALTER TABLE indexer_exp.repos_contributors
    ADD COLUMN has_completed_contribution BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE indexer_exp.repos_contributors
    ALTER COLUMN has_completed_contribution DROP DEFAULT;