package com.onlydust.marketplace.indexer.domain.services.events;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanIssue;
import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;
import com.onlydust.marketplace.indexer.domain.models.clean.IssueEvent;
import com.onlydust.marketplace.indexer.domain.models.raw.RawIssueEvent;
import com.onlydust.marketplace.indexer.domain.ports.in.Exposer;
import com.onlydust.marketplace.indexer.domain.ports.in.events.EventHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.transaction.Transactional;

@AllArgsConstructor
@Slf4j
@Transactional
public class IssueEventProcessorService implements EventHandler<RawIssueEvent> {
    private final Exposer<CleanIssue> issueExposer;
    private final Exposer<CleanRepo> repoExposer;

    @Override
    public void process(RawIssueEvent rawEvent) {
        final var event = IssueEvent.of(rawEvent);
        issueExposer.expose(event.getIssue());
        repoExposer.expose(event.getIssue().getRepo());
    }
}
