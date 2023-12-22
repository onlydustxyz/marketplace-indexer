DELETE
FROM indexer_exp.contributions
WHERE code_review_id IN (SELECT id
                         FROM indexer_exp.github_code_reviews
                         WHERE state = 'COMMENTED');

DELETE
FROM indexer_exp.github_code_reviews
WHERE state = 'COMMENTED';
