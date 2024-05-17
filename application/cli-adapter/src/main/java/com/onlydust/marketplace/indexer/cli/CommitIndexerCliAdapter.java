package com.onlydust.marketplace.indexer.cli;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanCommit;
import com.onlydust.marketplace.indexer.domain.ports.in.contexts.GithubAppContext;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageReader;
import com.onlydust.marketplace.indexer.postgres.entities.RepoIndexingJobEntity;
import com.onlydust.marketplace.indexer.postgres.entities.raw.RawPullRequestCommitsEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.RepoIndexingJobEntityRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubPullRequestRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.raw.PullRequestCommitsRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.raw.PullRequestRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.util.StopWatch;

import static com.onlydust.marketplace.indexer.domain.models.exposition.GithubPullRequest.extractMainFileExtensions;

@AllArgsConstructor
@Slf4j
@Profile("cli")
public class CommitIndexerCliAdapter implements CommandLineRunner {
    PullRequestCommitsRepository pullRequestCommitsRepository;
    PullRequestRepository pullRequestRepository;
    RawStorageReader rawStorageReader;
    RepoIndexingJobEntityRepository repoIndexingJobEntityRepository;
    GithubAppContext githubAppContext;
    GithubPullRequestRepository githubPullRequestRepository;

    @Override
    public void run(String... args) {
        if (args.length == 0 || !args[0].equals("index_commits")) return;

        final var stopWatch = new StopWatch();
        stopWatch.start();
        try {
            pullRequestCommitsRepository.findAll().stream().map(this::index).forEach(this::expose);
        } finally {
            stopWatch.stop();
            LOGGER.info("Indexing commits took {} ms", stopWatch.prettyPrint());
        }
    }

    private RawPullRequestCommitsEntity index(RawPullRequestCommitsEntity pullRequestCommits) {
        if (pullRequestCommits.getData().stream().findFirst().map(d -> d.getFiles() != null).isPresent()) return pullRequestCommits;

        LOGGER.info("Indexing commits for pull request {}", pullRequestCommits.getPullRequestId());

        final var pullRequest = pullRequestRepository.findById(pullRequestCommits.getPullRequestId())
                .orElseThrow(() -> new IllegalArgumentException("Raw pull request %d not found".formatted(pullRequestCommits.getPullRequestId())));

        final var installationId = repoIndexingJobEntityRepository.findById(pullRequest.getRepo().getData().getId())
                .map(RepoIndexingJobEntity::getInstallationId)
                .orElse(null);

        githubAppContext.withGithubApp(installationId,
                () -> rawStorageReader.pullRequestCommits(pullRequest.getRepo().getId(), pullRequest.getId(), pullRequest.getNumber())
                        .ifPresent(pullRequestCommits::setData)
        );

        pullRequestCommitsRepository.save(pullRequestCommits);

        return pullRequestCommits;
    }

    private void expose(RawPullRequestCommitsEntity pullRequestCommits) {
        LOGGER.info("Exposing modified files for pull request {}", pullRequestCommits.getPullRequestId());
        final var mainFileExtensions = extractMainFileExtensions(pullRequestCommits.getData().stream().map(c -> CleanCommit.of(c, null)).toList());

        if (mainFileExtensions.isEmpty()) {
            LOGGER.warn("No main file extensions found for pull request {}", pullRequestCommits.getPullRequestId());
            return;
        }

        githubPullRequestRepository.findById(pullRequestCommits.getPullRequestId()).ifPresentOrElse(
                pr -> {
                    pr.setMainFileExtensions(mainFileExtensions.toArray(String[]::new));
                    githubPullRequestRepository.save(pr);
                },
                () -> LOGGER.warn("Pull request {} not found", pullRequestCommits.getPullRequestId()));
    }
}
