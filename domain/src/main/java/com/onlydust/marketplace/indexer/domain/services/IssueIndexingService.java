package com.onlydust.marketplace.indexer.domain.services;

import com.onlydust.marketplace.indexer.domain.exception.OnlyDustException;
import com.onlydust.marketplace.indexer.domain.models.clean.CleanIssue;
import com.onlydust.marketplace.indexer.domain.ports.in.IssueIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.RepoIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.UserIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.RawStorageReader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class IssueIndexingService implements IssueIndexer {
    private final RawStorageReader rawStorageReader;
    private final UserIndexer userIndexer;
    private final RepoIndexer repoIndexer;

    @Override
    public CleanIssue indexIssue(String repoOwner, String repoName, Long issueNumber) {
        LOGGER.info("Indexing issue {} for repo {}/{}", issueNumber, repoOwner, repoName);
        final var repo = repoIndexer.indexRepo(repoOwner, repoName);
        final var issue = rawStorageReader.issue(repo.getId(), issueNumber).orElseThrow(() -> OnlyDustException.notFound("Issue not found"));
        final var assignees = issue.getAssignees().stream().map(assignee -> userIndexer.indexUser(assignee.getId())).toList();

        return CleanIssue.of(
                issue,
                repo,
                userIndexer.indexUser(issue.getAuthor().getId()),
                assignees
        );
    }
}
