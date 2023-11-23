CREATE TYPE indexer_exp.github_repo_visibility AS ENUM ('PUBLIC', 'PRIVATE');

ALTER TABLE indexer_exp.github_repos
    ADD visibility indexer_exp.github_repo_visibility NOT NULL DEFAULT 'PUBLIC';

ALTER TABLE indexer_exp.github_repos
    ALTER COLUMN visibility DROP DEFAULT;

UPDATE indexer_exp.github_repos exp
SET visibility = 'PRIVATE'
FROM indexer_raw.repos raw
WHERE exp.id = raw.id
  AND raw.data ->> 'visibility' = 'private';
