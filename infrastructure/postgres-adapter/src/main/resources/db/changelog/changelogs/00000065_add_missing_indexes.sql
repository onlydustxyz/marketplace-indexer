create unique index if not exists github_issues_labels_label_id_issue_id_uindex
    on indexer_exp.github_issues_labels (label_id, issue_id);

create index if not exists github_labels_name_index
    on indexer_exp.github_labels using gin (name public.gin_trgm_ops);
