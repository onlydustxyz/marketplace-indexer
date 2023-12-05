package com.onlydust.marketplace.indexer.domain.services.exposers;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanIssue;
import com.onlydust.marketplace.indexer.domain.models.exposition.Contribution;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubAccount;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubIssue;
import com.onlydust.marketplace.indexer.domain.ports.in.Exposer;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.ContributionStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.IssueStorage;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class IssueExposer implements Exposer<CleanIssue> {
    ContributionStorage contributionStorage;
    IssueStorage issueStorage;

    @Override
    public void expose(CleanIssue issue) {
        final var fromAssignees = issue.getAssignees().stream().map(GithubAccount::of).map(assignee -> Contribution.of(GithubIssue.of(issue), assignee));

        final var contributions = fromAssignees.toArray(Contribution[]::new);

        contributionStorage.deleteAllByRepoIdAndGithubNumber(issue.getRepo().getId(), issue.getNumber());
        contributionStorage.saveAll(contributions);
        issueStorage.save(GithubIssue.of(issue));
    }
}
