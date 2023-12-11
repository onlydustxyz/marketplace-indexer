UPDATE indexer_raw.events_inbox
SET payload = CAST(CONVERT_FROM(DECODE(payload #>> '{}', 'base64'), 'UTF8') AS JSONB);

ALTER TABLE indexer_raw.events_inbox
    ADD installation_id BIGINT GENERATED ALWAYS AS ((payload #>> '{installation,id}')::BIGINT) STORED;
