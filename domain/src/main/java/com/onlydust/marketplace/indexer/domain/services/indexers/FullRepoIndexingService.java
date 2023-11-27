package com.onlydust.marketplace.indexer.domain.services.indexers;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.IssueIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.PullRequestIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.RepoIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageReader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@AllArgsConstructor
@Slf4j
public class FullRepoIndexingService implements RepoIndexer {
    private final RawStorageReader rawStorageReader;
    private final IssueIndexer issueIndexer;
    private final PullRequestIndexer pullRequestIndexer;
    private final RepoIndexer repoIndexer;

    @Override
    public Optional<CleanRepo> indexRepo(Long repoId) {
        LOGGER.debug("Indexing full repo {}", repoId);
        return repoIndexer.indexRepo(repoId).map(repo -> {
            indexPullRequests(repo);
            indexIssues(repo);
            return repo;
        });
    }

    @Override
    public Optional<CleanRepo> indexRepo(String repoOwner, String repoName) {
        LOGGER.debug("Indexing full repo {}/{}", repoOwner, repoName);
        return repoIndexer.indexRepo(repoOwner, repoName).map(repo -> {
            indexPullRequests(repo);
            indexIssues(repo);
            return repo;
        });
    }

    private void indexPullRequests(CleanRepo repo) {
        rawStorageReader.repoPullRequests(repo.getId()).forEach(pr -> {
            try {
                pullRequestIndexer.indexPullRequest(repo.getOwner().getLogin(), repo.getName(), pr.getNumber());
            } catch (Exception e) {
                LOGGER.error("Unable to index pull request {} for repo {}", pr.getNumber(), repo.getId(), e);
            }
        });
    }

    private void indexIssues(CleanRepo repo) {
        rawStorageReader.repoIssues(repo.getId()).forEach(issue -> {
            try {
                issueIndexer.indexIssue(repo.getOwner().getLogin(), repo.getName(), issue.getNumber());
            } catch (Exception e) {
                LOGGER.error("Unable to index issue {} for repo {}", issue.getNumber(), repo.getId(), e);
            }
        });
    }
}
