package com.onlydust.marketplace.indexer.domain.services.exposers;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanIssue;
import com.onlydust.marketplace.indexer.domain.models.exposition.Contribution;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubAccount;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubIssue;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.IssueIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.ContributionStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.IssueStorage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@AllArgsConstructor
@Slf4j
public class IssueExposer implements IssueIndexer {
    IssueIndexer indexer;
    ContributionStorage contributionStorage;
    IssueStorage issueStorage;

    @Override
    public Optional<CleanIssue> indexIssue(String repoOwner, String repoName, Long issueNumber) {
        final var issue = indexer.indexIssue(repoOwner, repoName, issueNumber);
        issue.ifPresentOrElse(this::expose, () -> LOGGER.warn("Issue {} not found, unable to expose", issueNumber));
        return issue;
    }

    private void expose(CleanIssue issue) {
        LOGGER.info("Exposing issue {}", issue.getNumber());
        final var fromAssignees = issue.getAssignees().stream().map(GithubAccount::of).map(assignee -> Contribution.of(GithubIssue.of(issue), assignee));

        final var contributions = fromAssignees.toArray(Contribution[]::new);

        contributionStorage.saveAll(contributions);
        issueStorage.saveAll(GithubIssue.of(issue));
    }
}
