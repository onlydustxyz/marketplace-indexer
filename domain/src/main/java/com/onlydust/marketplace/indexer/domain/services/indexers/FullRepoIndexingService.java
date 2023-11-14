package com.onlydust.marketplace.indexer.domain.services.indexers;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.FullRepoIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.IssueIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.PullRequestIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.RepoIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageReader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@AllArgsConstructor
@Slf4j
public class FullRepoIndexingService implements FullRepoIndexer {
    private final RawStorageReader rawStorageReader;
    private final IssueIndexer issueIndexer;
    private final PullRequestIndexer pullRequestIndexer;
    private final RepoIndexer repoIndexer;

    @Override
    public Optional<CleanRepo> indexFullRepo(Long repoId) {
        LOGGER.debug("Indexing full repo {}", repoId);
        return repoIndexer.indexRepo(repoId).map(repo -> {
            rawStorageReader.repoPullRequests(repoId).forEach(pr -> pullRequestIndexer.indexPullRequest(repo.getOwner().getLogin(), repo.getName(), pr.getNumber()));
            rawStorageReader.repoIssues(repoId).forEach(issue -> issueIndexer.indexIssue(repo.getOwner().getLogin(), repo.getName(), issue.getNumber()));
            return repo;
        });
    }
}
