alter table indexer_exp.github_app_installations
    add column permissions text[] not null default '{issues:read,metadata:read,pull_requests:read}';

alter table indexer_exp.github_app_installations
    alter column permissions drop default;

