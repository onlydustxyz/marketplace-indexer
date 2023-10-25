package com.onlydust.marketplace.indexer.domain.services;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;
import com.onlydust.marketplace.indexer.domain.ports.in.FullRepoIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.IssueIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.PullRequestIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.RepoIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.RawStorageReader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class FullRepoIndexingService implements FullRepoIndexer {
    private final RawStorageReader rawStorageReader;
    private final IssueIndexer issueIndexer;
    private final PullRequestIndexer pullRequestIndexer;
    private final RepoIndexer repoIndexer;

    @Override
    public CleanRepo indexFullRepo(Long repoId) {
        LOGGER.info("Indexing full repo {}", repoId);
        final var repo = repoIndexer.indexRepo(repoId);
        rawStorageReader.repoPullRequests(repoId).forEach(pr -> pullRequestIndexer.indexPullRequest(repo.getOwner().getLogin(), repo.getName(), pr.getNumber()));
        rawStorageReader.repoIssues(repoId).forEach(issue -> issueIndexer.indexIssue(repo.getOwner().getLogin(), repo.getName(), issue.getNumber()));
        return repo;
    }
}
