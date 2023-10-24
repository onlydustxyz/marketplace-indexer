create table indexer_exp.github_app_installations (
    id bigint primary key,
    account_id bigint not null references indexer_exp.github_accounts(id));

insert into indexer_exp.github_app_installations (id, account_id)
    select installation_id, id from indexer_exp.github_accounts where installation_id is not null;

alter table indexer_exp.github_accounts
    add column name text,
    drop column installation_id;

