package com.onlydust.marketplace.indexer.domain.services.jobs;

import com.onlydust.marketplace.indexer.domain.jobs.Job;
import com.onlydust.marketplace.indexer.domain.jobs.RepoIndexerJob;
import com.onlydust.marketplace.indexer.domain.ports.in.contexts.GithubAppContext;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.RepoIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.RepoRefreshJobManager;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.RepoIndexingJobStorage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Slf4j
public class RepoRefreshJobService implements RepoRefreshJobManager {
    private final RepoIndexingJobStorage repoIndexingJobStorage;
    private final RepoIndexer repoIndexer;
    private final GithubAppContext githubAppContext;
    private final Config config;

    @Override
    public void addRepoToRefresh(Long repoId) {
        repoIndexingJobStorage.configureRepoForFullIndexing(repoId);
    }

    @Override
    public List<Job> allJobs() {
        return repoIndexingJobStorage.installationIds().stream()
                .map(this::createJobForInstallationId)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    private Optional<Job> createJobForInstallationId(Long installationId) {
        final var repos = repoIndexingJobStorage.reposUpdatedBefore(installationId, Instant.now().minusSeconds(config.refreshInterval));
        return repos.isEmpty() ? Optional.empty() : Optional.of(new RepoIndexerJob(repoIndexer, installationId, repos, repoIndexingJobStorage, githubAppContext));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Config {
        Integer refreshInterval;
    }
}
