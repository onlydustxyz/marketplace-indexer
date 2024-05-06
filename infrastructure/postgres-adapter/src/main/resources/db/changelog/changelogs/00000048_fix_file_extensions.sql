with corrected as (select id, array_agg(LOWER(extension)) as new_extensions
                   from indexer_exp.github_pull_requests,
                        unnest(main_file_extensions) extension
                   where extension not like '%/%'
                   group by id),
     all_corrected as (select gpr.id, coalesce(new_extensions, '{}') as new_extensions
                       from indexer_exp.github_pull_requests gpr
                                left join corrected c on c.id = gpr.id
                       WHERE NOT (gpr.main_file_extensions @> coalesce(new_extensions, '{}') AND
                                  gpr.main_file_extensions <@ coalesce(new_extensions, '{}')))
update indexer_exp.github_pull_requests gpr
set main_file_extensions = new_extensions
from all_corrected ac
where gpr.id = ac.id;


update indexer_exp.contributions c
set main_file_extensions = pr.main_file_extensions
from indexer_exp.github_pull_requests pr
where c.pull_request_id is not null
  and pr.id = c.pull_request_id
  and c.main_file_extensions != pr.main_file_extensions
;
