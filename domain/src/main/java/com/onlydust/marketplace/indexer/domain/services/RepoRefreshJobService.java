package com.onlydust.marketplace.indexer.domain.services;

import com.onlydust.marketplace.indexer.domain.jobs.Job;
import com.onlydust.marketplace.indexer.domain.jobs.RepoIndexerJob;
import com.onlydust.marketplace.indexer.domain.ports.in.FullRepoIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.GithubAppContext;
import com.onlydust.marketplace.indexer.domain.ports.in.RepoRefreshJobManager;
import com.onlydust.marketplace.indexer.domain.ports.out.RepoIndexingJobRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@AllArgsConstructor
@Slf4j
public class RepoRefreshJobService implements RepoRefreshJobManager {
    private final RepoIndexingJobRepository repoIndexingJobRepository;
    private final FullRepoIndexer fullRepoIndexer;
    private final GithubAppContext githubAppContext;

    @Override
    public void addRepoToRefresh(Long repoId) {
        repoIndexingJobRepository.add(0L, repoId);
    }

    @Override
    public List<Job> allJobs() {
        return repoIndexingJobRepository.installationIds().stream().map(this::createJobForInstallationId).toList();
    }

    private Job createJobForInstallationId(Long installationId) {
        final var repos = repoIndexingJobRepository.repos(installationId);
        return new RepoIndexerJob(fullRepoIndexer, installationId, repos, githubAppContext);
    }
}
