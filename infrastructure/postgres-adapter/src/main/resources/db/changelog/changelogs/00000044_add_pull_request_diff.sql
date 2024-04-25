CREATE TABLE indexer_raw.pull_requests_diff
(
    pull_request_id BIGINT PRIMARY KEY REFERENCES indexer_raw.pull_requests (id),
    data            JSONB     NOT NULL,
    tech_created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    tech_updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TRIGGER indexer_raw_pull_requests_diff_set_tech_updated_at
    BEFORE UPDATE
    ON
        indexer_raw.pull_requests_diff
    FOR EACH ROW
EXECUTE PROCEDURE set_tech_updated_at();

alter table indexer_exp.github_pull_requests
    add column main_file_extensions text[];