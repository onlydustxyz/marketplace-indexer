package com.onlydust.marketplace.indexer.domain.services.exposers;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanIssue;
import com.onlydust.marketplace.indexer.domain.models.exposition.Contribution;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubAccount;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubIssue;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.IssueIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.ContributionStorage;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class IssueContributionExposer implements IssueIndexer {
    IssueIndexer indexer;
    ContributionStorage expositionRepository;

    @Override
    public Optional<CleanIssue> indexIssue(String repoOwner, String repoName, Long issueNumber) {
        final var issue = indexer.indexIssue(repoOwner, repoName, issueNumber);
        issue.ifPresent(this::expose);
        return issue;
    }

    private void expose(CleanIssue issue) {
        final var fromAssignees = issue.getAssignees().stream().map(GithubAccount::of).map(assignee -> Contribution.of(GithubIssue.of(issue), assignee));

        final var contributions = fromAssignees.toArray(Contribution[]::new);

        expositionRepository.saveAll(contributions);
    }
}
