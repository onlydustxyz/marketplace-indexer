ALTER TABLE indexer_raw.pull_request_closing_issues
    ALTER CONSTRAINT pull_request_closing_issues_issue_id_fkey DEFERRABLE INITIALLY DEFERRED;
