package com.onlydust.marketplace.indexer.domain.services.jobs;

import com.onlydust.marketplace.indexer.domain.jobs.Job;
import com.onlydust.marketplace.indexer.domain.jobs.ParallelJobComposite;
import com.onlydust.marketplace.indexer.domain.jobs.RepoIndexerJob;
import com.onlydust.marketplace.indexer.domain.jobs.SequentialJobComposite;
import com.onlydust.marketplace.indexer.domain.models.RepoIndexingJobTrigger;
import com.onlydust.marketplace.indexer.domain.ports.in.contexts.GithubAppContext;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.RepoIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.JobManager;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.RepoIndexingJobStorage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.Executor;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toUnmodifiableSet;

@AllArgsConstructor
@Slf4j
public class RepoRefreshJobService implements JobManager {
    private final Executor executor;
    private final RepoIndexingJobStorage repoIndexingJobStorage;
    private final RepoIndexer fullRepoIndexer;
    private final RepoIndexer lightRepoIndexer;
    private final GithubAppContext githubAppContext;
    private final Config config;

    @Override
    public Job createJob() {
        return new ParallelJobComposite(executor, repoIndexingJobStorage.installationIds().stream()
                .map(this::createJobForInstallationId)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList()
        );
    }

    private Optional<Job> createJobForInstallationId(Long installationId) {
        final var repos = repoIndexingJobStorage.reposUpdatedBefore(installationId, Instant.now().minusSeconds(installationId == null ?
                config.unauthorizedReposRefreshInterval : config.authorizedReposRefreshInterval));

        if (repos.isEmpty()) return Optional.empty();

        final var fullIndexingRepos = repos.stream()
                .filter(RepoIndexingJobTrigger::getFullIndexing)
                .map(RepoIndexingJobTrigger::getRepoId)
                .collect(toUnmodifiableSet());

        final var lightIndexingRepos = repos.stream()
                .filter(r -> !r.getFullIndexing())
                .sorted(comparing(RepoIndexingJobTrigger::getRepoId))
                .limit(config.unauthorizedReposRefreshLimit)
                .map(RepoIndexingJobTrigger::getRepoId)
                .collect(toUnmodifiableSet());

        if (fullIndexingRepos.isEmpty())
            return Optional.of(new RepoIndexerJob(lightRepoIndexer, installationId, lightIndexingRepos, repoIndexingJobStorage, githubAppContext));

        if (lightIndexingRepos.isEmpty())
            return Optional.of(new RepoIndexerJob(fullRepoIndexer, installationId, fullIndexingRepos, repoIndexingJobStorage, githubAppContext));

        return Optional.of(new SequentialJobComposite(
                new RepoIndexerJob(fullRepoIndexer, installationId, fullIndexingRepos, repoIndexingJobStorage, githubAppContext),
                new RepoIndexerJob(lightRepoIndexer, installationId, lightIndexingRepos, repoIndexingJobStorage, githubAppContext)
        ));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Config {
        Integer unauthorizedReposRefreshInterval;
        Integer unauthorizedReposRefreshLimit;
        Integer authorizedReposRefreshInterval;
    }
}
