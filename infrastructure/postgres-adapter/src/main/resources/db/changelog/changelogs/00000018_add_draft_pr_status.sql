ALTER TYPE indexer_exp.github_pull_request_status ADD VALUE 'DRAFT';
COMMIT;

UPDATE indexer_exp.github_pull_requests
SET status = 'DRAFT'
WHERE status = 'OPEN'
  AND draft;

UPDATE indexer_exp.contributions
SET github_status = 'DRAFT'
FROM indexer_exp.github_pull_requests pr
WHERE pr.id = contributions.pull_request_id
  AND pr.status = 'DRAFT';

ALTER TABLE indexer_exp.contributions
    DROP COLUMN draft;
