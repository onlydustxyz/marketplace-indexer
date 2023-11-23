UPDATE indexer_exp.contributions
SET completed_at = pr.closed_at
FROM indexer_exp.github_code_reviews cr
         JOIN indexer_exp.github_pull_requests pr ON pr.id = cr.pull_request_id
WHERE contributions.code_review_id = cr.id
  AND pr.status = 'CLOSED';

UPDATE indexer_exp.contributions
SET completed_at = pr.merged_at
FROM indexer_exp.github_code_reviews cr
         JOIN indexer_exp.github_pull_requests pr ON pr.id = cr.pull_request_id
WHERE contributions.code_review_id = cr.id
  AND pr.status = 'MERGED';
