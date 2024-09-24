package com.onlydust.marketplace.indexer.domain.ports.out;

import com.onlydust.marketplace.indexer.domain.models.raw.github_app_events.RawIssueCommentEvent;
import com.onlydust.marketplace.indexer.domain.models.raw.github_app_events.RawIssueEvent;
import com.onlydust.marketplace.indexer.domain.models.raw.github_app_events.RawPullRequestEvent;

public interface GithubObserver {
    void on(RawIssueEvent event);

    void on(RawPullRequestEvent event);

    void on(RawIssueCommentEvent event);
}
