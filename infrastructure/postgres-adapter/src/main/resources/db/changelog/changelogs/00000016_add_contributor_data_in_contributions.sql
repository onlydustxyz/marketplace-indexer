ALTER TABLE indexer_exp.contributions
    ADD contributor_login      TEXT,
    ADD contributor_html_url   TEXT,
    ADD contributor_avatar_url TEXT
;

UPDATE indexer_exp.contributions
SET contributor_login      = a.login,
    contributor_html_url   = a.html_url,
    contributor_avatar_url = a.avatar_url
FROM indexer_exp.github_accounts a
WHERE contributions.contributor_id = a.id;

ALTER TABLE indexer_exp.contributions
    ALTER COLUMN contributor_login SET NOT NULL,
    ALTER COLUMN contributor_html_url SET NOT NULL,
    ALTER COLUMN contributor_avatar_url SET NOT NULL
;