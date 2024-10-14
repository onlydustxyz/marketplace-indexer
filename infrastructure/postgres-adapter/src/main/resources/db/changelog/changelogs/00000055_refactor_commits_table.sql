create schema if not exists tmp;

alter table indexer_raw.pull_request_commits
    set schema tmp;

create view tmp.commits as
with all_commits as (select jsonb_array_elements(c.data) as data,
                            c.pull_request_id            as pull_request_id,
                            pr.repo_id                   as repo_id,
                            c.tech_created_at            as tech_created_at,
                            c.tech_updated_at            as tech_updated_at
                     from tmp.pull_request_commits c
                              join indexer_raw.pull_requests pr on c.pull_request_id = pr.id)
select c.data #>> '{sha}'                  as sha,
       c.repo_id                           as repo_id,
       c.pull_request_id                   as pull_request_id,
       (c.data #>> '{author, id}')::bigint as author_id,
       c.data #>> '{author, login}'        as author_login,
       c.data                              as data,
       c.tech_created_at                   as tech_created_at,
       c.tech_updated_at                   as tech_updated_at
from all_commits c;


create table indexer_raw.commits
(
    sha             text primary key,
    repo_id         bigint                   not null,
    author_id       bigint,
    author_name     text,
    data            jsonb,
    tech_created_at timestamp with time zone not null default now(),
    tech_updated_at timestamp with time zone not null default now()
);

create trigger indexer_raw_commits_set_tech_updated_at
    before update
    on indexer_raw.commits
    for each row
execute function indexer.set_tech_updated_at();

insert
into indexer_raw.commits(sha, repo_id, author_id, author_name, data, tech_created_at, tech_updated_at)
select c.sha             as sha,
       c.repo_id         as repo_id,
       c.author_id       as author_id,
       c.author_login    as author_name,
       c.data            as data,
       c.tech_created_at as tech_created_at,
       c.tech_updated_at as tech_updated_at
from tmp.commits c;

create table indexer_raw.pull_request_commits
(
    pull_request_id bigint                   not null,
    commit_sha      text                     not null,
    tech_created_at timestamp with time zone not null default now(),
    tech_updated_at timestamp with time zone not null default now(),
    primary key (pull_request_id, commit_sha)
);

create trigger indexer_raw_pull_request_commits_set_tech_updated_at
    before update
    on indexer_raw.pull_request_commits
    for each row
execute function indexer.set_tech_updated_at();


insert
into indexer_raw.pull_request_commits(pull_request_id, commit_sha, tech_created_at, tech_updated_at)
select pull_request_id, sha, tech_created_at, tech_updated_at
from tmp.commits c;


drop schema tmp cascade;


alter table indexer_raw.public_events
    alter column tech_created_at set default now();

alter table indexer_raw.public_events
    alter column tech_updated_at set default now();

create table indexer_exp.github_user_file_extensions
(
    user_id            bigint                   not null,
    file_extension     text                     not null,
    commit_count       int                      not null,
    file_count         int                      not null,
    modification_count int                      not null,
    tech_created_at    timestamp with time zone not null default now(),
    tech_updated_at    timestamp with time zone not null default now(),
    primary key (user_id, file_extension)
);

create trigger indexer_exp_github_user_file_extensions_set_tech_updated_at
    before update
    on indexer_exp.github_user_file_extensions
    for each row
execute function indexer.set_tech_updated_at();

create table indexer_exp.github_commits
(
    sha             text primary key,
    pull_request_id bigint references indexer_exp.github_pull_requests (id),
    author_id       bigint references indexer_exp.github_accounts (id),
    tech_created_at timestamp with time zone not null default now(),
    tech_updated_at timestamp with time zone not null default now()
);

create trigger indexer_exp_github_commits_set_tech_updated_at
    before update
    on indexer_exp.github_commits
    for each row
execute function indexer.set_tech_updated_at();

insert into indexer_exp.github_commits(sha, pull_request_id, author_id, tech_created_at, tech_updated_at)
select prc.commit_sha, prc.pull_request_id, c.author_id, prc.tech_created_at, prc.tech_updated_at
from indexer_raw.pull_request_commits prc
         join indexer_raw.commits c on prc.commit_sha = c.sha;
