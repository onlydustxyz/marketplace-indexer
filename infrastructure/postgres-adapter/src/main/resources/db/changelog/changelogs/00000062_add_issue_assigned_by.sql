alter table indexer_exp.github_issues_assignees
    add column assigned_by_user_id bigint references indexer_exp.github_accounts (id);

create unique index on indexer_exp.github_issues_assignees (issue_id, user_id, assigned_by_user_id);
create unique index on indexer_exp.github_issues_assignees (user_id, issue_id, assigned_by_user_id);
create unique index on indexer_exp.github_issues_assignees (assigned_by_user_id, user_id, issue_id);