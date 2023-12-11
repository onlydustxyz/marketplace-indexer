UPDATE indexer_raw.events_inbox
SET payload = CAST(CONVERT_FROM(DECODE(payload #>> '{}', 'base64'), 'UTF8') AS JSONB);
