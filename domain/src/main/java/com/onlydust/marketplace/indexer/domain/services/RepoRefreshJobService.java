package com.onlydust.marketplace.indexer.domain.services;

import com.onlydust.marketplace.indexer.domain.ports.in.RepoRefreshJobManager;
import com.onlydust.marketplace.indexer.domain.ports.out.RepoIndexingJobRepository;
import com.onlydust.marketplace.indexer.domain.ports.out.RepoRefresher;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class RepoRefreshJobService implements RepoRefreshJobManager {
    private final RepoIndexingJobRepository repoIndexingJobRepository;
    private final RepoRefresher repoRefresher;

    @Override
    public void addRepoToRefresh(Long repoId) {
        repoIndexingJobRepository.add(0L, repoId);
    }

    @Override
    public void runAllJobs() {
        repoIndexingJobRepository.installationIds()
                .forEach(this::scheduleJob);
    }

    private void scheduleJob(Long installationId) {
        final var repos = repoIndexingJobRepository.repos(installationId);
        repoRefresher.refreshRepos(installationId, repos);
    }
}
