package com.onlydust.marketplace.indexer.domain.services.events;

import com.onlydust.marketplace.indexer.domain.models.clean.IssueEvent;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubIssue;
import com.onlydust.marketplace.indexer.domain.models.raw.RawIssueEvent;
import com.onlydust.marketplace.indexer.domain.ports.in.events.EventHandler;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.IssueStorage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.transaction.Transactional;

@AllArgsConstructor
@Slf4j
@Transactional
public class IssueEventProcessorService implements EventHandler<RawIssueEvent> {
    private final IssueStorage githubIssueStorage;


    @Override
    public void process(RawIssueEvent rawEvent) {
        final var event = IssueEvent.of(rawEvent);
        githubIssueStorage.save(GithubIssue.of(event.getIssue()));
    }
}
