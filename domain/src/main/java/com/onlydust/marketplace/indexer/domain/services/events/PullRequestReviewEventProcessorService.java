package com.onlydust.marketplace.indexer.domain.services.events;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;
import com.onlydust.marketplace.indexer.domain.models.raw.github_app_events.RawPullRequestReviewEvent;
import com.onlydust.marketplace.indexer.domain.ports.in.Exposer;
import com.onlydust.marketplace.indexer.domain.ports.in.contexts.GithubAppContext;
import com.onlydust.marketplace.indexer.domain.ports.in.events.EventHandler;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.PullRequestIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageReader;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageWriter;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static java.util.stream.Collectors.toList;

@AllArgsConstructor
@Slf4j
@Transactional
public class PullRequestReviewEventProcessorService implements EventHandler<RawPullRequestReviewEvent> {
    private final Exposer<CleanRepo> repoExposer;
    private final RawStorageReader rawStorageReader;
    private final RawStorageWriter rawStorageWriter;
    private final PullRequestIndexer pullRequestIndexer;
    private final GithubAppContext githubAppContext;

    @Override
    public void process(RawPullRequestReviewEvent event) {

        final var reviews = rawStorageReader.pullRequestReviews(event.getRepository().getId(), event.getPullRequest().getId(),
                        event.getPullRequest().getNumber())
                .orElse(List.of())
                .stream()
                .filter(r -> !r.getId().equals(event.getReview().getId()))
                .collect(toList());

        reviews.add(event.getReview());

        rawStorageWriter.savePullRequestReviews(event.getPullRequest().getId(), reviews);

        githubAppContext.withGithubApp(event.getInstallation().getId(), () ->
                pullRequestIndexer.indexPullRequest(event.getRepository().getOwner().getLogin(),
                        event.getRepository().getName(),
                        event.getPullRequest().getNumber()
                ).ifPresent(pr -> repoExposer.expose(pr.getRepo()))
        );
    }
}
