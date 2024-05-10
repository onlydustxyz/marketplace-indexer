alter table indexer_exp.github_accounts
    add column created_at timestamp;

update indexer_exp.github_accounts ga
set created_at = (u.data ->> 'created_at')::date
from indexer_raw.users u
where u.id = ga.id;
