package com.onlydust.marketplace.indexer.domain.services.indexers;

import java.time.ZonedDateTime;

import com.onlydust.marketplace.indexer.domain.models.raw.RawIssue;
import com.onlydust.marketplace.indexer.domain.models.raw.RawPullRequest;
import com.onlydust.marketplace.indexer.domain.models.raw.public_events.RawIssuesEventPayload;
import com.onlydust.marketplace.indexer.domain.models.raw.public_events.RawPublicEvent;
import com.onlydust.marketplace.indexer.domain.models.raw.public_events.RawPullRequestEventPayload;
import com.onlydust.marketplace.indexer.domain.models.raw.public_events.RawPushEventPayload;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.IssueIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.PullRequestIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.UserPublicEventsIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.UserPublicEventsIndexingJobStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.PublicEventRawStorageReader;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageReader;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageWriter;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class UserPublicEventsIndexingService implements UserPublicEventsIndexer {
    private final PublicEventRawStorageReader publicEventRawStorageReader;
    private final UserPublicEventsIndexingJobStorage userPublicEventsIndexingJobStorage;
    private final RawStorageWriter rawStorageWriter;
    private final RawStorageReader rawStorageReader;

    private final PullRequestIndexer pullRequestIndexer;
    private final IssueIndexer issueIndexer;
    @Override
    public void indexUser(final @NonNull Long userId, final @NonNull ZonedDateTime since) {
        LOGGER.info("Indexing public events for user {} since {}", userId, since);
        publicEventRawStorageReader.userPublicEvents(userId, since)
                .forEach(this::index);
    }

    @Override
    public void indexAllUsers(final @NonNull ZonedDateTime timestamp) {
        final var userIds = userPublicEventsIndexingJobStorage.all();
        LOGGER.info("Indexing public events for {} users at {}", userIds.size(), timestamp);
        publicEventRawStorageReader.allPublicEvents(timestamp)
                .filter(event -> userIds.contains(event.actor().getId()))
                .forEach(this::index);
    }

    private void index(final @NonNull RawPublicEvent event) {
        event.decode().ifPresentOrElse(payload -> index(event, payload), () -> LOGGER.debug("Unknown event type: {}", event.type()));
        userPublicEventsIndexingJobStorage.saveLastEventTimestamp(event.actor().getId(), event.createdAt());
    }

    private void index(final @NonNull RawPublicEvent event, final @NonNull RawPublicEvent.Payload rawPayload) {
        if (rawPayload instanceof RawPullRequestEventPayload payload)
            index(event, payload);
        else if (rawPayload instanceof RawPushEventPayload payload)
            index(event, payload);
        else if (rawPayload instanceof RawIssuesEventPayload payload)
            index(event, payload);
    }

    private void index(final @NonNull RawPublicEvent event, RawPushEventPayload payload) {
        rawStorageWriter.saveCommits(event.repo().getId(), payload.commits());
    }

    private void index(final @NonNull RawPullRequest pullRequest) {
        rawStorageWriter.savePullRequest(pullRequest);

        rawStorageReader.repo(pullRequest.getBase().getRepo().getId())
                .ifPresent(repo -> {
                    rawStorageReader.repoLanguages(repo.getId());
                    rawStorageReader.user(repo.getOwner().getId())
                            .ifPresent(owner -> pullRequestIndexer.indexPullRequest(owner.getLogin(), repo.getName(), pullRequest.getNumber()));
                });
    }

    private void index(final @NonNull RawPublicEvent event, final @NonNull RawPullRequestEventPayload payload) {
        LOGGER.debug("Indexing event: {}", event);
        index(payload.pullRequest());
    }

    private void index(final @NonNull RawPublicEvent event, final @NonNull RawIssuesEventPayload payload) {
        LOGGER.debug("Indexing event: {}", event);
        index(event.repo().getId(), payload.issue());
    }

    private void index(final @NonNull Long repoId, final @NonNull RawIssue issue) {
        rawStorageWriter.saveIssue(repoId, issue);

        rawStorageReader.repo(repoId)
                .ifPresent(repo -> {
                    rawStorageReader.repoLanguages(repo.getId());
                    rawStorageReader.user(repo.getOwner().getId())
                            .ifPresent(owner -> issueIndexer.indexIssue(owner.getLogin(), repo.getName(), issue.getNumber()));
                });
    }
}
