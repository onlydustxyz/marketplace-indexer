CREATE TABLE indexer_exp.github_labels
(
    id              BIGINT PRIMARY KEY,
    name            TEXT      NOT NULL,
    description     TEXT,
    tech_created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    tech_updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TRIGGER indexer_exp_github_labels_set_tech_updated_at
    BEFORE UPDATE
    ON
        indexer_exp.github_labels
    FOR EACH ROW
EXECUTE PROCEDURE set_tech_updated_at();

CREATE TABLE indexer_exp.github_issues_labels
(
    issue_id        BIGINT    NOT NULL REFERENCES indexer_exp.github_issues (id),
    label_id        BIGINT    NOT NULL REFERENCES indexer_exp.github_labels (id),
    tech_created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    tech_updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (issue_id, label_id)
);

CREATE TRIGGER indexer_exp_github_issues_labels_set_tech_updated_at
    BEFORE UPDATE
    ON
        indexer_exp.github_issues_labels
    FOR EACH ROW
EXECUTE PROCEDURE set_tech_updated_at();