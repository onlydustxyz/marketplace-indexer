UPDATE indexer_exp.contributions c
SET status='CANCELLED'
FROM indexer_exp.github_code_reviews cr
         JOIN indexer_exp.github_pull_requests pr on cr.pull_request_id = pr.id
where c.code_review_id = cr.id
  and c.status = 'IN_PROGRESS'
  and pr.status in ('MERGED', 'CLOSED');