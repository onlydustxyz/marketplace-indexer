CREATE TYPE indexer_exp.github_pull_request_review_state AS ENUM (
    'PENDING_REVIEWER', 'UNDER_REVIEW', 'APPROVED', 'CHANGES_REQUESTED'
    );

ALTER TABLE indexer_exp.github_pull_requests
    ADD review_state indexer_exp.github_pull_request_review_state
;

UPDATE indexer_exp.github_pull_requests
SET review_state = 'CHANGES_REQUESTED'
WHERE id IN (SELECT pull_request_id
             FROM indexer_exp.github_code_reviews cr
             WHERE cr.pull_request_id = github_pull_requests.id
               AND cr.state = 'CHANGES_REQUESTED');

UPDATE indexer_exp.github_pull_requests
SET review_state = 'APPROVED'
WHERE review_state IS NULL
  AND id IN (SELECT pull_request_id
             FROM indexer_exp.github_code_reviews cr
             WHERE cr.pull_request_id = github_pull_requests.id
               AND cr.state = 'APPROVED');

UPDATE indexer_exp.github_pull_requests
SET review_state = 'UNDER_REVIEW'
WHERE review_state IS NULL
  AND id IN (SELECT pull_request_id
             FROM indexer_exp.github_code_reviews cr
             WHERE cr.pull_request_id = github_pull_requests.id
               AND cr.state = 'COMMENTED');

UPDATE indexer_exp.github_pull_requests
SET review_state = 'PENDING_REVIEWER'
WHERE review_state IS NULL;

ALTER TABLE indexer_exp.github_pull_requests
    ALTER COLUMN review_state SET NOT NULL;


ALTER TABLE indexer_exp.contributions
    ADD pr_review_state indexer_exp.github_pull_request_review_state
;

UPDATE indexer_exp.contributions
SET pr_review_state = pr.review_state
FROM indexer_exp.github_pull_requests pr
WHERE pr.id = contributions.pull_request_id;
