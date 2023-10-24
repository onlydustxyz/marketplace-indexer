package com.onlydust.marketplace.indexer.domain.services;

import com.onlydust.marketplace.indexer.domain.exception.OnlyDustException;
import com.onlydust.marketplace.indexer.domain.models.clean.CleanIssue;
import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;
import com.onlydust.marketplace.indexer.domain.ports.in.IssueIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.UserIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.RawStorageReader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class IssueIndexingService implements IssueIndexer {
    private final RawStorageReader rawStorageReader;
    private final UserIndexer userIndexer;

    @Override
    public CleanIssue indexIssue(String repoOwner, String repoName, Long issueNumber) {
        LOGGER.info("Indexing issue {} for repo {}/{}", issueNumber, repoOwner, repoName);
        final var repo = rawStorageReader.repo(repoOwner, repoName).orElseThrow(() -> OnlyDustException.notFound("Repo not found"));
        final var issue = rawStorageReader.issue(repo.getId(), issueNumber).orElseThrow(() -> OnlyDustException.notFound("Issue not found"));
        final var assignees = issue.getAssignees().stream().map(assignee -> userIndexer.indexUser(assignee.getId())).toList();
        final var repoOwnerAccount = userIndexer.indexUser(repo.getOwner().getId());

        return CleanIssue.of(
                issue,
                CleanRepo.of(repo, repoOwnerAccount),
                userIndexer.indexUser(issue.getAuthor().getId()),
                assignees
        );
    }
}
