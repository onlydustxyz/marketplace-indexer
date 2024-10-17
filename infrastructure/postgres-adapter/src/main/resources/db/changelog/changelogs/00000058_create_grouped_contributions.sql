create extension "uuid-ossp";


CREATE OR REPLACE FUNCTION indexer.uuid_of(github_unique_id text)
    RETURNS UUID AS
$$
SELECT uuid_generate_v3('00000000-0000-0000-0000-000000000000'::uuid, github_unique_id)
$$ LANGUAGE SQL
    IMMUTABLE
    PARALLEL SAFE;



alter table indexer_exp.contributions
    add column contribution_uuid uuid;

update indexer_exp.contributions
set contribution_uuid = indexer.uuid_of(coalesce(pull_request_id::text, issue_id::text, code_review_id));

alter table indexer_exp.contributions
    alter column contribution_uuid set not null;

create index contributions_contribution_uuid_index
    on indexer_exp.contributions (contribution_uuid);



alter table indexer_exp.github_issues
    add column contribution_uuid uuid;

update indexer_exp.github_issues
set contribution_uuid = indexer.uuid_of(id::text);

alter table indexer_exp.github_issues
    alter column contribution_uuid set not null;

create unique index github_issues_contribution_uuid_index
    on indexer_exp.github_issues (contribution_uuid);



alter table indexer_exp.github_pull_requests
    add column contribution_uuid uuid;

update indexer_exp.github_pull_requests
set contribution_uuid = indexer.uuid_of(id::text);

alter table indexer_exp.github_pull_requests
    alter column contribution_uuid set not null;

create unique index github_pull_requests_contribution_uuid_index
    on indexer_exp.github_pull_requests (contribution_uuid);



alter table indexer_exp.github_code_reviews
    add column contribution_uuid uuid;

update indexer_exp.github_code_reviews
set contribution_uuid = indexer.uuid_of(id::text);

alter table indexer_exp.github_code_reviews
    alter column contribution_uuid set not null;

create unique index github_code_reviews_contribution_uuid_index
    on indexer_exp.github_code_reviews (contribution_uuid);



create table indexer_exp.grouped_contributions
(
    contribution_uuid        uuid primary key,
    repo_id                  bigint                          not null references indexer_exp.github_repos,
    type                     indexer_exp.contribution_type   not null,
    status                   indexer_exp.contribution_status not null,
    pull_request_id          bigint references indexer_exp.github_pull_requests,
    issue_id                 bigint references indexer_exp.github_issues,
    code_review_id           text references indexer_exp.github_code_reviews,
    created_at               timestamp                       not null,
    updated_at               timestamp with time zone        not null,
    completed_at             timestamp,
    github_number            bigint                          not null,
    github_status            text                            not null,
    github_title             text                            not null,
    github_html_url          text                            not null,
    github_body              text,
    github_comments_count    integer                         not null,
    repo_owner_login         text                            not null,
    repo_name                text                            not null,
    repo_html_url            text                            not null,
    github_author_id         bigint                          not null references indexer_exp.github_accounts,
    github_author_login      text                            not null,
    github_author_html_url   text                            not null,
    github_author_avatar_url text                            not null,
    pr_review_state          indexer_exp.github_pull_request_review_state,
    main_file_extensions     text[],
    tech_created_at          timestamp default now()         not null,
    tech_updated_at          timestamp default now()         not null,
    constraint grouped_contributions_check
        check (((type = 'PULL_REQUEST'::indexer_exp.contribution_type) AND (pull_request_id IS NOT NULL) AND (issue_id IS NULL) AND (code_review_id IS NULL)) OR
               ((type = 'ISSUE'::indexer_exp.contribution_type) AND (pull_request_id IS NULL) AND (issue_id IS NOT NULL) AND (code_review_id IS NULL)) OR
               ((type = 'CODE_REVIEW'::indexer_exp.contribution_type) AND (pull_request_id IS NULL) AND (issue_id IS NULL) AND (code_review_id IS NOT NULL)))
);

create trigger grouped_contributions_set_tech_updated_at
    before update
    on indexer_exp.grouped_contributions
    for each row
execute function set_tech_updated_at();

create index grouped_contributions_code_review_id_as_text_index
    on indexer_exp.grouped_contributions (code_review_id);

create index grouped_contributions_completed_at_created_at_desc_idx
    on indexer_exp.grouped_contributions (COALESCE(completed_at, created_at) desc);

create index grouped_contributions_completed_at_created_at_idx
    on indexer_exp.grouped_contributions (COALESCE(completed_at, created_at));

create index grouped_contributions_completed_at_index
    on indexer_exp.grouped_contributions (completed_at);

create index grouped_contributions_created_at_index
    on indexer_exp.grouped_contributions (created_at);

create index grouped_contributions_github_author_id_index
    on indexer_exp.grouped_contributions (github_author_id);

create index grouped_contributions_pull_request_id_index
    on indexer_exp.grouped_contributions (pull_request_id);

create index grouped_contributions_repo_id_index
    on indexer_exp.grouped_contributions (repo_id);

create index grouped_contributions_status_as_text_index
    on indexer_exp.grouped_contributions (status);

create index grouped_contributions_type_as_text_index
    on indexer_exp.grouped_contributions (type);


create table indexer_exp.grouped_contribution_contributors
(
    contribution_uuid uuid                    not null references indexer_exp.grouped_contributions,
    contributor_id    bigint                  not null references indexer_exp.github_accounts,
    tech_created_at   timestamp default now() not null,
    tech_updated_at   timestamp default now() not null,
    primary key (contribution_uuid, contributor_id)
);

create trigger grouped_contribution_contributors_set_tech_updated_at
    before update
    on indexer_exp.grouped_contribution_contributors
    for each row
execute function set_tech_updated_at();

create unique index grouped_contribution_contributors_pk_inv
    on indexer_exp.grouped_contribution_contributors (contributor_id, contribution_uuid);


INSERT INTO indexer_exp.grouped_contributions (contribution_uuid,
                                               repo_id,
                                               type,
                                               status,
                                               pull_request_id,
                                               issue_id,
                                               code_review_id,
                                               created_at,
                                               updated_at,
                                               completed_at,
                                               github_number,
                                               github_status,
                                               github_title,
                                               github_html_url,
                                               github_body,
                                               github_comments_count,
                                               repo_owner_login,
                                               repo_name,
                                               repo_html_url,
                                               github_author_id,
                                               github_author_login,
                                               github_author_html_url,
                                               github_author_avatar_url,
                                               pr_review_state,
                                               main_file_extensions)
SELECT DISTINCT ON (c.contribution_uuid) c.contribution_uuid,
                                         c.repo_id,
                                         c.type,
                                         c.status,
                                         c.pull_request_id,
                                         c.issue_id,
                                         c.code_review_id,
                                         c.created_at,
                                         c.updated_at,
                                         c.completed_at,
                                         c.github_number,
                                         c.github_status,
                                         c.github_title,
                                         c.github_html_url,
                                         c.github_body,
                                         c.github_comments_count,
                                         c.repo_owner_login,
                                         c.repo_name,
                                         c.repo_html_url,
                                         c.github_author_id,
                                         c.github_author_login,
                                         c.github_author_html_url,
                                         c.github_author_avatar_url,
                                         c.pr_review_state,
                                         c.main_file_extensions
FROM indexer_exp.contributions c;


INSERT INTO indexer_exp.grouped_contribution_contributors (contribution_uuid, contributor_id)
SELECT c.contribution_uuid, c.contributor_id
FROM indexer_exp.contributions c;



