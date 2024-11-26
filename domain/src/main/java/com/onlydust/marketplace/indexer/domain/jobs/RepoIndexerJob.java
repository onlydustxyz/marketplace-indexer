package com.onlydust.marketplace.indexer.domain.jobs;

import com.onlydust.marketplace.indexer.domain.ports.in.contexts.GithubAppContext;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.RepoIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.RepoIndexingJobStorage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@AllArgsConstructor
@Slf4j
public class RepoIndexerJob extends Job {
    final RepoIndexer fullRepoIndexer;
    final Long installationId;
    final Set<Long> repoIds;
    final RepoIndexingJobStorage repoIndexingJobStorage;
    final GithubAppContext githubAppContext;

    @Override
    public void execute() {
        githubAppContext.withGithubApp(installationId,
                () -> repoIds.forEach(repo -> {
                    try {
                        LOGGER.info("Indexing repo {}", repo);
                        repoIndexingJobStorage.startJob(repo);
                        if (fullRepoIndexer.indexRepo(repo).isEmpty())
                            LOGGER.warn("Repo {} not found", repo);
                        repoIndexingJobStorage.endJob(repo);
                    } catch (Throwable e) {
                        LOGGER.error("Failed to index repo {}", repo, e);
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
