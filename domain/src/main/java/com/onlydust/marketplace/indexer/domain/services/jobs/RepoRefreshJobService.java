package com.onlydust.marketplace.indexer.domain.services.jobs;

import com.onlydust.marketplace.indexer.domain.jobs.Job;
import com.onlydust.marketplace.indexer.domain.jobs.RepoIndexerJob;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.FullRepoIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.contexts.GithubAppContext;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.RepoRefreshJobManager;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.RepoIndexingJobStorage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@AllArgsConstructor
@Slf4j
public class RepoRefreshJobService implements RepoRefreshJobManager {
    private final RepoIndexingJobStorage repoIndexingJobStorage;
    private final FullRepoIndexer fullRepoIndexer;
    private final GithubAppContext githubAppContext;

    @Override
    public void addRepoToRefresh(Long repoId) {
        repoIndexingJobStorage.add(0L, repoId);
    }

    @Override
    public List<Job> allJobs() {
        return repoIndexingJobStorage.installationIds().stream().map(this::createJobForInstallationId).toList();
    }

    private Job createJobForInstallationId(Long installationId) {
        final var repos = repoIndexingJobStorage.repos(installationId);
        return new RepoIndexerJob(fullRepoIndexer, installationId, repos, githubAppContext);
    }
}