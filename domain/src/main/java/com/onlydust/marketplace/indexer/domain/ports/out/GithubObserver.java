package com.onlydust.marketplace.indexer.domain.ports.out;

import com.onlydust.marketplace.indexer.domain.models.raw.RawIssueCommentEvent;
import com.onlydust.marketplace.indexer.domain.models.raw.RawIssueEvent;
import com.onlydust.marketplace.indexer.domain.models.raw.RawPullRequestEvent;

public interface GithubObserver {
    void on(RawIssueEvent event);

    void on(RawPullRequestEvent event);

    void on(RawIssueCommentEvent event);
}
