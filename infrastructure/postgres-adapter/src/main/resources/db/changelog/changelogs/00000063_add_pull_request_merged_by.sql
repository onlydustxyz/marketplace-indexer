alter table indexer_exp.github_pull_requests
    add column merged_by_id bigint references indexer_exp.github_accounts (id);


-- Best effort: set merged_by_id for existing pull requests where the user is already exposed
update indexer_exp.github_pull_requests
set merged_by_id = ga.id
from indexer_raw.pull_requests pr
         join indexer_exp.github_accounts ga on ga.id = (pr.data -> 'merged_by' ->> 'id')::bigint
where github_pull_requests.id = pr.id
  and github_pull_requests.merged_by_id is null;
