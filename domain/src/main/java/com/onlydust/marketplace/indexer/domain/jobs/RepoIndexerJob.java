package com.onlydust.marketplace.indexer.domain.jobs;

import com.onlydust.marketplace.indexer.domain.ports.in.contexts.GithubAppContext;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.FullRepoIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.RepoIndexingJobStorage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@AllArgsConstructor
@Slf4j
public class RepoIndexerJob extends Job {
    final FullRepoIndexer fullRepoIndexer;
    final Long installationId;
    final Set<Long> repoIds;
    final RepoIndexingJobStorage repoIndexingJobStorage;
    GithubAppContext githubAppContext;

    @Override
    public void execute() {
        LOGGER.info("Indexing installation {} for repos {}", installationId, repoIds);
        githubAppContext.withGithubApp(installationId,
                () -> repoIds.forEach(repo -> {
                    try {
                        repoIndexingJobStorage.startJob(repo);
                        if (fullRepoIndexer.indexFullRepo(repo).isEmpty())
                            LOGGER.warn("Repo {} not found", repo);
                        repoIndexingJobStorage.endJob(repo);
                    } catch (Throwable e) {
                        LOGGER.error("Error indexing repo {}", repo, e);
                        repoIndexingJobStorage.failJob(repo);
                    }
                })
        );
    }

    @Override
    public String name() {
        return String.format("repo-indexer-%d", installationId);
    }
}
