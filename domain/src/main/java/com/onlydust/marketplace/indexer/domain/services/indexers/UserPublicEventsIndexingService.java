package com.onlydust.marketplace.indexer.domain.services.indexers;

import com.onlydust.marketplace.indexer.domain.models.raw.RawAccount;
import com.onlydust.marketplace.indexer.domain.models.raw.RawPullRequest;
import com.onlydust.marketplace.indexer.domain.models.raw.RawRepo;
import com.onlydust.marketplace.indexer.domain.models.raw.public_events.RawPublicEvent;
import com.onlydust.marketplace.indexer.domain.models.raw.public_events.RawPullRequestEventPayload;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.*;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.PublicEventRawStorageReader;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageReader;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageWriter;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;

@AllArgsConstructor
@Slf4j
public class UserPublicEventsIndexingService implements UserPublicEventsIndexer {
    private final PublicEventRawStorageReader publicEventRawStorageReader;
    private final RawStorageWriter rawStorageWriter;
    private final RawStorageReader rawStorageReader;

    private final RepoIndexer repoIndexer;
    private final UserIndexer userIndexer;
    private final PullRequestIndexer pullRequestIndexer;
    private final IssueIndexer issueIndexer;

    @Override
    public void indexUser(final @NonNull Long userId, final @NonNull ZonedDateTime since) {
        LOGGER.debug("Indexing stats for user {} since {}", userId, since);
        publicEventRawStorageReader.userPublicEvents(userId, since)
                .distinct()
                .forEach(this::index);
    }

    private void index(final @NonNull RawPublicEvent event) {
        event.decode().ifPresentOrElse(this::index, () -> LOGGER.debug("Unknown event type: {}", event.type()));
    }

    private void index(final @NonNull RawPublicEvent.Payload rawPayload) {
        if (rawPayload instanceof RawPullRequestEventPayload payload)
            index(payload);
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

    private void index(final @NonNull RawPullRequestEventPayload event) {
        LOGGER.debug("Indexing event: {}", event);
        index(event.pullRequest());
    }
}
