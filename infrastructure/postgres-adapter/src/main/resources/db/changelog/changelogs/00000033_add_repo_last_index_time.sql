CREATE TABLE indexer_exp.github_repos_stats
(
    id              BIGINT PRIMARY KEY REFERENCES indexer_exp.github_repos (id),
    last_indexed_at TIMESTAMP
);

INSERT INTO indexer_exp.github_repos_stats
SELECT repo_id, finished_at
FROM indexer.repo_indexing_jobs;
