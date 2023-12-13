CREATE TABLE indexer_exp.github_repo_languages
(
    repo_id         BIGINT    NOT NULL,
    language        TEXT      NOT NULL,
    line_count      BIGINT    NOT NULL,
    tech_created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    tech_updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (repo_id, language)
);

CREATE TRIGGER indexer_exp_github_repo_languages_set_tech_updated_at
    BEFORE UPDATE
    ON
        indexer_exp.github_repo_languages
    FOR EACH ROW
EXECUTE PROCEDURE set_tech_updated_at();
