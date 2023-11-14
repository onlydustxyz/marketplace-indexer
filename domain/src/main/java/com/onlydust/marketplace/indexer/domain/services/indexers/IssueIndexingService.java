package com.onlydust.marketplace.indexer.domain.services.indexers;

import com.onlydust.marketplace.indexer.domain.exception.OnlyDustException;
import com.onlydust.marketplace.indexer.domain.models.clean.CleanIssue;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.IssueIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.RepoIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.UserIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageReader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Optional;

@AllArgsConstructor
@Slf4j
public class IssueIndexingService implements IssueIndexer {
    private final RawStorageReader rawStorageReader;
    private final UserIndexer userIndexer;
    private final RepoIndexer repoIndexer;

    @Override
    public Optional<CleanIssue> indexIssue(String repoOwner, String repoName, Long issueNumber) {
        LOGGER.debug("Indexing issue {} for repo {}/{}", issueNumber, repoOwner, repoName);
        return repoIndexer.indexRepo(repoOwner, repoName).flatMap(repo -> {
            final var issue = rawStorageReader.issue(repo.getId(), issueNumber).orElseThrow(() -> OnlyDustException.notFound("Issue not found"));
            return userIndexer.indexUser(issue.getAuthor().getId()).map(author -> {
                final var assignees = issue.getAssignees().stream().map(assignee -> userIndexer.indexUser(assignee.getId()).orElseGet(() -> {
                    LOGGER.warn("User {} not found, skipping assignee {}", assignee.getId(), assignee.getLogin());
                    return null;
                })).filter(Objects::nonNull).toList();

                return CleanIssue.of(
                        issue,
                        repo,
                        author,
                        assignees
                );
            });
        });
    }
}
