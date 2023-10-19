CREATE TYPE indexer_exp.github_account_type AS ENUM ('USER', 'ORGANIZATION');

ALTER TABLE indexer_exp.github_accounts
    ALTER COLUMN type TYPE indexer_exp.github_account_type
        USING type::indexer_exp.github_account_type;
