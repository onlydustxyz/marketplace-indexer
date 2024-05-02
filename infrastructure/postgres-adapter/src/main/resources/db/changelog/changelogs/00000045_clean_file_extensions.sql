with corrected as (select id, array_agg(extension) new_extensions
                   from indexer_exp.github_pull_requests,
                        unnest(main_file_extensions) extension
                   where extension not like '%/%'
                   group by id)
update indexer_exp.github_pull_requests gpr
set main_file_extensions = new_extensions
from corrected
where main_file_extensions <> new_extensions
  and gpr.id = corrected.id;