CREATE FUNCTION set_tech_updated_at()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.tech_updated_at = now();
    RETURN NEW;
END;
$$ language 'plpgsql';

-- indexer_raw.installation_events
ALTER TABLE indexer_raw.installation_events
    RENAME COLUMN created_at TO tech_created_at;

ALTER TABLE indexer_raw.installation_events
    ALTER COLUMN tech_created_at SET DEFAULT now(),
    ADD tech_updated_at TIMESTAMP NOT NULL DEFAULT now();

CREATE TRIGGER indexer_raw_installation_events_set_tech_updated_at
    BEFORE UPDATE
    ON
        indexer_raw.installation_events
    FOR EACH ROW
EXECUTE PROCEDURE set_tech_updated_at();

-- indexer_raw.repo_check_runs
ALTER TABLE indexer_raw.repo_check_runs
    RENAME COLUMN created_at TO tech_created_at;

ALTER TABLE indexer_raw.repo_check_runs
    RENAME COLUMN updated_at TO tech_updated_at;

ALTER TABLE indexer_raw.repo_check_runs
    ALTER COLUMN tech_created_at SET DEFAULT now(),
    ALTER COLUMN tech_updated_at SET DEFAULT now()
;

CREATE TRIGGER indexer_raw_repo_check_runs_set_tech_updated_at
    BEFORE UPDATE
    ON
        indexer_raw.repo_check_runs
    FOR EACH ROW
EXECUTE PROCEDURE set_tech_updated_at();

-- indexer_raw.repos
ALTER TABLE indexer_raw.repos
    RENAME COLUMN created_at TO tech_created_at;

ALTER TABLE indexer_raw.repos
    RENAME COLUMN updated_at TO tech_updated_at;

ALTER TABLE indexer_raw.repos
    ALTER COLUMN tech_created_at SET DEFAULT now(),
    ALTER COLUMN tech_updated_at SET DEFAULT now()
;

CREATE TRIGGER indexer_raw_repos_set_tech_updated_at
    BEFORE UPDATE
    ON
        indexer_raw.repos
    FOR EACH ROW
EXECUTE PROCEDURE set_tech_updated_at();

-- indexer_raw.issues
ALTER TABLE indexer_raw.issues
    RENAME COLUMN created_at TO tech_created_at;

ALTER TABLE indexer_raw.issues
    RENAME COLUMN updated_at TO tech_updated_at;

ALTER TABLE indexer_raw.issues
    ALTER COLUMN tech_created_at SET DEFAULT now(),
    ALTER COLUMN tech_updated_at SET DEFAULT now()
;

CREATE TRIGGER indexer_raw_issues_set_tech_updated_at
    BEFORE UPDATE
    ON
        indexer_raw.issues
    FOR EACH ROW
EXECUTE PROCEDURE set_tech_updated_at();

-- indexer_raw.pull_requests
ALTER TABLE indexer_raw.pull_requests
    RENAME COLUMN created_at TO tech_created_at;

ALTER TABLE indexer_raw.pull_requests
    RENAME COLUMN updated_at TO tech_updated_at;

ALTER TABLE indexer_raw.pull_requests
    ALTER COLUMN tech_created_at SET DEFAULT now(),
    ALTER COLUMN tech_updated_at SET DEFAULT now()
;

CREATE TRIGGER indexer_raw_pull_requests_set_tech_updated_at
    BEFORE UPDATE
    ON
        indexer_raw.pull_requests
    FOR EACH ROW
EXECUTE PROCEDURE set_tech_updated_at();

-- indexer_raw.pull_request_closing_issues
ALTER TABLE indexer_raw.pull_request_closing_issues
    RENAME COLUMN created_at TO tech_created_at;

ALTER TABLE indexer_raw.pull_request_closing_issues
    RENAME COLUMN updated_at TO tech_updated_at;

ALTER TABLE indexer_raw.pull_request_closing_issues
    ALTER COLUMN tech_created_at SET DEFAULT now(),
    ALTER COLUMN tech_updated_at SET DEFAULT now()
;

CREATE TRIGGER indexer_raw_pull_request_closing_issues_set_tech_updated_at
    BEFORE UPDATE
    ON
        indexer_raw.pull_request_closing_issues
    FOR EACH ROW
EXECUTE PROCEDURE set_tech_updated_at();


-- indexer_raw.pull_request_commits
ALTER TABLE indexer_raw.pull_request_commits
    RENAME COLUMN created_at TO tech_created_at;

ALTER TABLE indexer_raw.pull_request_commits
    RENAME COLUMN updated_at TO tech_updated_at;

ALTER TABLE indexer_raw.pull_request_commits
    ALTER COLUMN tech_created_at SET DEFAULT now(),
    ALTER COLUMN tech_updated_at SET DEFAULT now()
;

CREATE TRIGGER indexer_raw_pull_request_commits_set_tech_updated_at
    BEFORE UPDATE
    ON
        indexer_raw.pull_request_commits
    FOR EACH ROW
EXECUTE PROCEDURE set_tech_updated_at();

-- indexer_raw.pull_request_reviews
ALTER TABLE indexer_raw.pull_request_reviews
    RENAME COLUMN created_at TO tech_created_at;

ALTER TABLE indexer_raw.pull_request_reviews
    RENAME COLUMN updated_at TO tech_updated_at;

ALTER TABLE indexer_raw.pull_request_reviews
    ALTER COLUMN tech_created_at SET DEFAULT now(),
    ALTER COLUMN tech_updated_at SET DEFAULT now()
;

CREATE TRIGGER indexer_raw_pull_request_reviews_set_tech_updated_at
    BEFORE UPDATE
    ON
        indexer_raw.pull_request_reviews
    FOR EACH ROW
EXECUTE PROCEDURE set_tech_updated_at();

-- indexer_raw.repo_languages
ALTER TABLE indexer_raw.repo_languages
    RENAME COLUMN created_at TO tech_created_at;

ALTER TABLE indexer_raw.repo_languages
    RENAME COLUMN updated_at TO tech_updated_at;

ALTER TABLE indexer_raw.repo_languages
    ALTER COLUMN tech_created_at SET DEFAULT now(),
    ALTER COLUMN tech_updated_at SET DEFAULT now()
;

CREATE TRIGGER indexer_raw_repo_languages_set_tech_updated_at
    BEFORE UPDATE
    ON
        indexer_raw.repo_languages
    FOR EACH ROW
EXECUTE PROCEDURE set_tech_updated_at();

-- indexer_raw.users
ALTER TABLE indexer_raw.users
    RENAME COLUMN created_at TO tech_created_at;

ALTER TABLE indexer_raw.users
    RENAME COLUMN updated_at TO tech_updated_at;

ALTER TABLE indexer_raw.users
    ALTER COLUMN tech_created_at SET DEFAULT now(),
    ALTER COLUMN tech_updated_at SET DEFAULT now()
;

CREATE TRIGGER indexer_raw_users_set_tech_updated_at
    BEFORE UPDATE
    ON
        indexer_raw.users
    FOR EACH ROW
EXECUTE PROCEDURE set_tech_updated_at();

-- indexer_raw.user_social_accounts
ALTER TABLE indexer_raw.user_social_accounts
    RENAME COLUMN created_at TO tech_created_at;

ALTER TABLE indexer_raw.user_social_accounts
    RENAME COLUMN updated_at TO tech_updated_at;

ALTER TABLE indexer_raw.user_social_accounts
    ALTER COLUMN tech_created_at SET DEFAULT now(),
    ALTER COLUMN tech_updated_at SET DEFAULT now()
;

CREATE TRIGGER indexer_raw_user_social_accounts_set_tech_updated_at
    BEFORE UPDATE
    ON
        indexer_raw.user_social_accounts
    FOR EACH ROW
EXECUTE PROCEDURE set_tech_updated_at();


-- indexer_exp.github_accounts
CREATE TRIGGER indexer_exp_github_accounts_set_tech_updated_at
    BEFORE UPDATE
    ON
        indexer_exp.github_accounts
    FOR EACH ROW
EXECUTE PROCEDURE set_tech_updated_at();

-- indexer_exp.github_app_installations
CREATE TRIGGER indexer_exp_github_app_installations_set_tech_updated_at
    BEFORE UPDATE
    ON
        indexer_exp.github_app_installations
    FOR EACH ROW
EXECUTE PROCEDURE set_tech_updated_at();

-- indexer_exp.github_repos
CREATE TRIGGER indexer_exp_github_repos_set_tech_updated_at
    BEFORE UPDATE
    ON
        indexer_exp.github_repos
    FOR EACH ROW
EXECUTE PROCEDURE set_tech_updated_at();

-- indexer_exp.github_issues
CREATE TRIGGER indexer_exp_github_issues_set_tech_updated_at
    BEFORE UPDATE
    ON
        indexer_exp.github_issues
    FOR EACH ROW
EXECUTE PROCEDURE set_tech_updated_at();

-- indexer_exp.github_pull_requests
CREATE TRIGGER indexer_exp_github_pull_requests_set_tech_updated_at
    BEFORE UPDATE
    ON
        indexer_exp.github_pull_requests
    FOR EACH ROW
EXECUTE PROCEDURE set_tech_updated_at();

-- indexer_exp.github_code_reviews
CREATE TRIGGER indexer_exp_github_code_reviews_set_tech_updated_at
    BEFORE UPDATE
    ON
        indexer_exp.github_code_reviews
    FOR EACH ROW
EXECUTE PROCEDURE set_tech_updated_at();

-- indexer_exp.contributions
CREATE TRIGGER indexer_exp_contributions_set_tech_updated_at
    BEFORE UPDATE
    ON
        indexer_exp.contributions
    FOR EACH ROW
EXECUTE PROCEDURE set_tech_updated_at();


-- indexer.repo_indexing_jobs
CREATE TRIGGER indexer_repo_indexing_jobs_set_tech_updated_at
    BEFORE UPDATE
    ON
        indexer.repo_indexing_jobs
    FOR EACH ROW
EXECUTE PROCEDURE set_tech_updated_at();


-- indexer.user_indexing_jobs
CREATE TRIGGER indexer_user_indexing_jobs_set_tech_updated_at
    BEFORE UPDATE
    ON
        indexer.user_indexing_jobs
    FOR EACH ROW
EXECUTE PROCEDURE set_tech_updated_at();
