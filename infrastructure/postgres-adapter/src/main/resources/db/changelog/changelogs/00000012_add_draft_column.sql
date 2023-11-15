ALTER TABLE indexer_exp.github_pull_requests
    ADD draft BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE indexer_exp.github_pull_requests
SET draft = (pr.data ->> 'draft')::BOOLEAN
FROM indexer_raw.pull_requests pr
WHERE pr.id = github_pull_requests.id;

ALTER TABLE indexer_exp.contributions
    ADD draft BOOLEAN;

UPDATE indexer_exp.contributions
SET draft = pr.draft
FROM indexer_exp.github_pull_requests pr
WHERE pr.id = contributions.pull_request_id;

