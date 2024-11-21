create unique index if not exists github_issues_labels_label_id_issue_id_uindex
    on indexer_exp.github_issues_labels (label_id, issue_id);

create index if not exists github_labels_name_index
    on indexer_exp.github_labels using gin (name public.gin_trgm_ops);

-- Not related to the feature, but recommended by Datadog
create unique index if not exists github_pull_requests_commits_pkey_inv
    on indexer_exp.github_pull_requests_commits (pull_request_id, commit_sha);

create index if not exists contributions_completed_at_main_file_extensions_status_type
    on indexer_exp.contributions (completed_at, main_file_extensions, status, type);
