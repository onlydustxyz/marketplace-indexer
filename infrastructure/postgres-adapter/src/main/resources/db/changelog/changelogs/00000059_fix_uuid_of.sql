CREATE OR REPLACE FUNCTION indexer.uuid_of(github_unique_id text)
    RETURNS UUID AS
$$
SELECT indexer.uuid_generate_v3('00000000-0000-0000-0000-000000000000'::uuid, github_unique_id)
$$ LANGUAGE SQL
    IMMUTABLE
    PARALLEL SAFE;

