update indexer.repo_indexing_jobs j
set is_public = (r.data ->> 'visibility' = 'public')
from indexer_raw.repos r
where j.repo_id = r.id
  and j.is_public != (r.data ->> 'visibility' = 'public');
