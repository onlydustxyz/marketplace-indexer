UPDATE indexer_exp.github_code_reviews
SET repo_html_url = r.html_url
FROM indexer_exp.github_repos r
WHERE repo_id = r.id;

UPDATE indexer_exp.contributions
SET repo_html_url = cr.repo_html_url,
    github_status = cr.state
FROM indexer_exp.github_code_reviews cr
WHERE contributions.code_review_id = cr.id;
