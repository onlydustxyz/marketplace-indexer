package com.onlydust.marketplace.indexer.domain.jobs;

import com.onlydust.marketplace.indexer.domain.models.raw.*;
import com.onlydust.marketplace.indexer.domain.ports.in.events.EventHandler;
import com.onlydust.marketplace.indexer.domain.ports.out.EventInboxStorage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.util.Optional;

@AllArgsConstructor
@Slf4j
public class OtherEventsInboxJob extends Job {
    private final EventInboxStorage eventInboxStorage;
    private final EventHandler<RawRepositoryEvent> repositoryEventHandler;
    private final EventHandler<RawStarEvent> starEventHandler;
    private final EventHandler<RawIssueEvent> issueEventHandler;
    private final EventHandler<RawIssueCommentEvent> issueCommentEventHandler;
    private final EventHandler<RawPullRequestEvent> pullRequestEventHandler;

    @Override
    protected void execute() {
        Optional<RawEvent> event;
        while ((event = eventInboxStorage.peek("repository", "star", "issues", "pull_request", "pull_request_review")).isPresent())
            process(event.get());
    }

    @Retryable(maxAttempts = 6, backoff = @Backoff(delay = 500, multiplier = 2))
    private void process(RawEvent event) {
        try {
            switch (event.getType()) {
                case "repository":
                    repositoryEventHandler.process(event.payload(RawRepositoryEvent.class));
                    eventInboxStorage.ack(event.getId());
                    break;
                case "star":
                    starEventHandler.process(event.payload(RawStarEvent.class));
                    eventInboxStorage.ack(event.getId());
                    break;
                case "issues":
                    issueEventHandler.process(event.payload(RawIssueEvent.class));
                    eventInboxStorage.ack(event.getId());
                    break;
                case "issue_comment":
                    issueCommentEventHandler.process(event.payload(RawIssueCommentEvent.class));
                    eventInboxStorage.ack(event.getId());
                    break;
                case "pull_request", "pull_request_review":
                    pullRequestEventHandler.process(event.payload(RawPullRequestEvent.class));
                    eventInboxStorage.ack(event.getId());
                    break;
            }
        } catch (Exception e) {
            LOGGER.error("Error processing event: {}", event.toString(), e);
            eventInboxStorage.nack(event.getId(), e.getMessage() == null ? e.toString() : e.getMessage());
        }
    }

    @Override
    public String name() {
        return "other-events-inbox-dequeuer";
    }
}
