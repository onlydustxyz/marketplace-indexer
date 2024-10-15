alter table indexer_exp.github_pull_requests
    add column updated_at timestamptz;

update indexer_exp.github_pull_requests
set updated_at = tech_updated_at;

alter table indexer_exp.github_pull_requests
    alter column updated_at set not null;

alter table indexer_exp.github_issues
    add column updated_at timestamptz;

update indexer_exp.github_issues
set updated_at = tech_updated_at;

alter table indexer_exp.github_issues
    alter column updated_at set not null;

alter table indexer_exp.contributions
    add column updated_at timestamptz;

update indexer_exp.contributions
set updated_at = pr.updated_at
from indexer_exp.github_pull_requests pr
where contributions.pull_request_id = pr.id;

update indexer_exp.contributions
set updated_at = i.updated_at
from indexer_exp.github_issues i
where contributions.issue_id = i.id;

update indexer_exp.contributions
set updated_at = coalesce(cr.submitted_at, cr.requested_at)
from indexer_exp.github_code_reviews cr
where contributions.code_review_id = cr.id;

alter table indexer_exp.contributions
    alter column updated_at set not null;
