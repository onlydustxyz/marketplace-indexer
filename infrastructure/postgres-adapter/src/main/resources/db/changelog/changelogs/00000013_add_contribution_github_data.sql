ALTER TABLE indexer_exp.contributions
    ADD github_number         BIGINT,
    ADD github_status         TEXT,
    ADD github_title          TEXT,
    ADD github_html_url       TEXT,
    ADD github_body           TEXT,
    ADD github_comments_count INT
;

UPDATE indexer_exp.contributions
SET github_number         = pr.number,
    github_status         = pr.status,
    github_title          = pr.title,
    github_html_url       = pr.html_url,
    github_body           = pr.body,
    github_comments_count = pr.comments_count
FROM indexer_exp.github_pull_requests pr
WHERE pr.id = contributions.pull_request_id;

UPDATE indexer_exp.contributions
SET github_number         = i.number,
    github_status         = i.status,
    github_title          = i.title,
    github_html_url       = i.html_url,
    github_body           = i.body,
    github_comments_count = i.comments_count
FROM indexer_exp.github_issues i
WHERE i.id = contributions.issue_id;

UPDATE indexer_exp.contributions
SET github_number         = pr.number,
    github_status         = pr.status,
    github_title          = pr.title,
    github_html_url       = pr.html_url,
    github_body           = pr.body,
    github_comments_count = pr.comments_count
FROM indexer_exp.github_code_reviews cr
         JOIN indexer_exp.github_pull_requests pr on pr.id = cr.pull_request_id
WHERE cr.id = contributions.code_review_id;

ALTER TABLE indexer_exp.contributions
    ALTER COLUMN github_number SET NOT NULL,
    ALTER COLUMN github_status SET NOT NULL,
    ALTER COLUMN github_title SET NOT NULL,
    ALTER COLUMN github_html_url SET NOT NULL,
    ALTER COLUMN github_comments_count SET NOT NULL
;