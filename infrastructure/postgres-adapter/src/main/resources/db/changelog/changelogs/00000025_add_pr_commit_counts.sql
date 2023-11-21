ALTER TABLE indexer_exp.github_pull_requests
    ADD COLUMN commit_count INT NOT NULL DEFAULT 0;

ALTER TABLE indexer_exp.github_pull_requests
    ALTER COLUMN commit_count DROP DEFAULT;

ALTER TABLE indexer_exp.contributions
    DROP CONSTRAINT contributions_contributor_id_repo_id_pull_request_id_issue__key,
    ADD CONSTRAINT contributions_unique_constraint UNIQUE (contributor_id, repo_id, pull_request_id, issue_id, code_review_id);

CREATE TABLE indexer_exp.github_pull_request_commit_counts
(
    pull_request_id BIGINT NOT NULL,
    author_id       BIGINT NOT NULL,
    commit_count    INT    NOT NULL,
    PRIMARY KEY (pull_request_id, author_id),
    FOREIGN KEY (pull_request_id) REFERENCES indexer_exp.github_pull_requests (id) DEFERRABLE INITIALLY DEFERRED,
    FOREIGN KEY (author_id) REFERENCES indexer_exp.github_accounts (id) DEFERRABLE INITIALLY DEFERRED
);