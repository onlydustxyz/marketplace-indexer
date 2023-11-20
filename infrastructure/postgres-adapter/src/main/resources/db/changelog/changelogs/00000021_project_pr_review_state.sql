CREATE TYPE indexer_exp.github_pull_request_review_state AS ENUM (
    'PENDING_REVIEWER', 'UNDER_REVIEW', 'APPROVED', 'CHANGES_REQUESTED'
    );

ALTER TABLE indexer_exp.github_pull_requests
    ADD review_state indexer_exp.github_pull_request_review_state NOT NULL DEFAULT 'PENDING_REVIEWER'
;

ALTER TABLE indexer_exp.github_pull_requests
    ALTER COLUMN review_state DROP DEFAULT
;

ALTER TABLE indexer_exp.contributions
    ADD pr_review_state indexer_exp.github_pull_request_review_state
;
