CREATE TABLE indexer.notifier_jobs
(
    id                SERIAL PRIMARY KEY,
    tech_created_at   timestamp          default now()                         not null,
    tech_updated_at   timestamp          default now()                         not null,
    last_notification timestamp,
    status            indexer.job_status default 'PENDING'::indexer.job_status not null,
    started_at        timestamp,
    finished_at       timestamp
);

CREATE TRIGGER indexer_notifier_jobs_set_tech_updated_at
    BEFORE UPDATE
    ON
        indexer.notifier_jobs
    FOR EACH ROW
EXECUTE PROCEDURE set_tech_updated_at();
