alter table indexer_exp.github_repos
    add column languages jsonb not null default '{}'::jsonb,
    add column has_issues boolean not null default true,
    add column parent_id bigint references indexer_exp.github_repos(id);

