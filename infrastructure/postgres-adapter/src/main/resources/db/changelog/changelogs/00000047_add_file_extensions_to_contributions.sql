alter table indexer_exp.contributions
    add column main_file_extensions text[];

update indexer_exp.contributions c
set main_file_extensions = pr.main_file_extensions
from indexer_exp.github_pull_requests pr
where c.pull_request_id is not null
  and pr.id = c.pull_request_id;
