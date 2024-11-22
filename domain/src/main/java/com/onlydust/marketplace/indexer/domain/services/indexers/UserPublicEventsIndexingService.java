package com.onlydust.marketplace.indexer.domain.services.indexers;

import com.onlydust.marketplace.indexer.domain.models.raw.RawAccount;
import com.onlydust.marketplace.indexer.domain.models.raw.RawPullRequest;
import com.onlydust.marketplace.indexer.domain.models.raw.RawRepo;
import com.onlydust.marketplace.indexer.domain.models.raw.public_events.RawPublicEvent;
import com.onlydust.marketplace.indexer.domain.models.raw.public_events.RawPullRequestEventPayload;
import com.onlydust.marketplace.indexer.domain.models.raw.public_events.RawPushEventPayload;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.*;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.UserPublicEventsIndexingJobStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.PublicEventRawStorageReader;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageReader;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageWriter;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;
import java.util.Set;

@AllArgsConstructor
@Slf4j
public class UserPublicEventsIndexingService implements UserPublicEventsIndexer {
    private final PublicEventRawStorageReader publicEventRawStorageReader;
    private final UserPublicEventsIndexingJobStorage userPublicEventsIndexingJobStorage;
    private final RawStorageWriter rawStorageWriter;
    private final RawStorageReader rawStorageReader;

    private final RepoIndexer repoIndexer;
    private final UserIndexer userIndexer;
    private final PullRequestIndexer pullRequestIndexer;
    private final IssueIndexer issueIndexer;

    @Override
    public void indexUser(final @NonNull Long userId, final @NonNull ZonedDateTime since) {
        LOGGER.info("Indexing public events for user {} since {}", userId, since);
        publicEventRawStorageReader.userPublicEvents(userId, since)
                .distinct()
                .forEach(this::index);
    }

    @Override
    public void indexUsers(final @NonNull Set<Long> userIds, final @NonNull ZonedDateTime since) {
        LOGGER.info("Indexing public events for {} users since {}", userIds.size(), since);
        publicEventRawStorageReader.allPublicEvents(since)
                .filter(event -> userIds.contains(event.actor().getId()))
                .distinct()
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
    }

    private void index(final @NonNull RawPublicEvent event, RawPushEventPayload payload) {
        rawStorageWriter.saveCommits(event.repo().getId(), payload.commits());
    }

    private void index(final @NonNull RawAccount user) {
        rawStorageWriter.saveUser(user);
        userIndexer.indexUser(user.getId());
    }

    private void index(final @NonNull RawRepo repo) {
        rawStorageWriter.saveRepo(repo);
        repoIndexer.indexRepo(repo.getId());
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
}
