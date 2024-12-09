-- First add the column allowing nulls to support the data migration
ALTER TABLE indexer_exp.github_accounts
    ADD COLUMN follower_count INTEGER;

-- Migrate existing data from raw storage
UPDATE indexer_exp.github_accounts ga
SET follower_count = (
    SELECT (ru.data->>'followers')::INTEGER
    FROM indexer_raw.users ru
    WHERE ru.id = ga.id
);

-- Set default value for any remaining nulls
UPDATE indexer_exp.github_accounts
SET follower_count = 0
WHERE follower_count IS NULL;

-- Make the column non-null after migration
ALTER TABLE indexer_exp.github_accounts
    ALTER COLUMN follower_count SET NOT NULL,
    ALTER COLUMN follower_count SET DEFAULT 0; 