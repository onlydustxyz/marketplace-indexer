CREATE TYPE indexer_raw.inbox_status AS ENUM ('PENDING', 'PROCESSED', 'FAILED', 'IGNORED');

CREATE TABLE indexer_raw.events_inbox
(
    id              SERIAL PRIMARY KEY,
    type            TEXT                     NOT NULL,
    status          indexer_raw.inbox_status NOT NULL DEFAULT 'PENDING',
    payload         JSONB                    NOT NULL,
    reason          TEXT,
    tech_created_at TIMESTAMP                NOT NULL DEFAULT NOW(),
    tech_updated_at TIMESTAMP                NOT NULL DEFAULT NOW()
);

CREATE TRIGGER indexer_raw_events_inbox_set_tech_updated_at
    BEFORE UPDATE
    ON
        indexer_raw.events_inbox
    FOR EACH ROW
EXECUTE PROCEDURE set_tech_updated_at();

DROP TABLE indexer_raw.installation_events;
