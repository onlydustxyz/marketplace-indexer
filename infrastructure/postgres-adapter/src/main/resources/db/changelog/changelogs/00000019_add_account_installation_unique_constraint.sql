-- delete all but the latest row for each account_id
delete
from indexer_exp.authorized_github_repos
where installation_id in (select id
                          from indexer_exp.github_app_installations
                          except
                          (select max(id)
                           from indexer_exp.github_app_installations
                           group by account_id));

delete
from indexer_exp.github_app_installations
where id in (select id
             from indexer_exp.github_app_installations
             except
             (select max(id)
              from indexer_exp.github_app_installations
              group by account_id));

CREATE UNIQUE INDEX github_app_installations_account_id_idx
    ON indexer_exp.github_app_installations (account_id);
