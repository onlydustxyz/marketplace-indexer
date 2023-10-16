package com.onlydust.marketplace.indexer.domain.services;

import com.onlydust.marketplace.indexer.domain.exception.OnlyDustException;
import com.onlydust.marketplace.indexer.domain.mappers.RepoMapper;
import com.onlydust.marketplace.indexer.domain.models.clean.Repo;
import com.onlydust.marketplace.indexer.domain.ports.in.IssueIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.PullRequestIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.RepoIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.RawStorageReader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class RepoIndexingService implements RepoIndexer {
    private final RawStorageReader rawStorageReader;
    private final IssueIndexer issueIndexer;
    private final PullRequestIndexer pullRequestIndexer;

    @Override
    public Repo indexRepo(Long repoId) {
        LOGGER.info("Indexing repo {}", repoId);
        final var repo = rawStorageReader.repo(repoId).orElseThrow(() -> OnlyDustException.notFound("Repo not found"));
        final var pullRequests = rawStorageReader.repoPullRequests(repoId).stream().map(pr -> pullRequestIndexer.indexPullRequest(repo.getOwner().getLogin(), repo.getName(), pr.getNumber())).toList();
        final var issues = rawStorageReader.repoIssues(repoId).stream().map(issue -> issueIndexer.indexIssue(repo.getOwner().getLogin(), repo.getName(), issue.getNumber())).toList();
        final var languages = rawStorageReader.repoLanguages(repoId);
        return RepoMapper.map(repo, pullRequests, issues, languages);
    }
}
