package com.onlydust.marketplace.indexer.domain.services.events;

import com.onlydust.marketplace.indexer.domain.models.raw.github_app_events.RawIssueCommentEvent;
import com.onlydust.marketplace.indexer.domain.ports.in.events.EventHandler;
import com.onlydust.marketplace.indexer.domain.ports.out.GithubObserver;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
@Transactional
public class IssueCommentEventProcessorService implements EventHandler<RawIssueCommentEvent> {
    private final GithubObserver githubObserver;

    @Override
    public void process(RawIssueCommentEvent event) {
        githubObserver.on(event);
    }
}
