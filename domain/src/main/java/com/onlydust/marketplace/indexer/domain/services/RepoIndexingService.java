package com.onlydust.marketplace.indexer.domain.services;

import com.onlydust.marketplace.indexer.domain.exception.OnlyDustException;
import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;
import com.onlydust.marketplace.indexer.domain.ports.in.IssueIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.PullRequestIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.RepoIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.UserIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.RawStorageReader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class RepoIndexingService implements RepoIndexer {
    private final RawStorageReader rawStorageReader;
    private final IssueIndexer issueIndexer;
    private final PullRequestIndexer pullRequestIndexer;
    private final UserIndexer userIndexer;

    @Override
    public CleanRepo indexRepo(Long repoId) {
        LOGGER.info("Indexing repo {}", repoId);
        final var repo = rawStorageReader.repo(repoId).orElseThrow(() -> OnlyDustException.notFound("Repo not found"));
        final var languages = rawStorageReader.repoLanguages(repoId);
        rawStorageReader.repoPullRequests(repoId).forEach(pr -> pullRequestIndexer.indexPullRequest(repo.getOwner().getLogin(), repo.getName(), pr.getNumber()));
        rawStorageReader.repoIssues(repoId).forEach(issue -> issueIndexer.indexIssue(repo.getOwner().getLogin(), repo.getName(), issue.getNumber()));
        final var repoOwnerAccount = userIndexer.indexUser(repo.getOwner().getId());
        return CleanRepo.of(
                repo,
                repoOwnerAccount,
                languages
        );
    }
}
