package com.onlydust.marketplace.indexer.domain.services.exposers;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanIssue;
import com.onlydust.marketplace.indexer.domain.models.exposition.Contribution;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubAccount;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubIssue;
import com.onlydust.marketplace.indexer.domain.ports.in.Exposer;
import com.onlydust.marketplace.indexer.domain.ports.out.IndexingObserver;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.ContributionStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.IssueStorage;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import java.util.stream.Stream;

@AllArgsConstructor
public class IssueExposer implements Exposer<CleanIssue> {
    private final ContributionStorage contributionStorage;
    private final IssueStorage issueStorage;
    private final IndexingObserver indexingObserver;

    @Override
    @Transactional
    public void expose(CleanIssue issue) {
        contributionStorage.deleteAllByRepoIdAndGithubNumber(issue.getRepo().getId(), issue.getNumber());

        final var contributions = contributionsOf(issue);
        contributionStorage.saveAll(contributions);
        Stream.of(contributions).map(Contribution::getContributionUUID).distinct()
                .forEach(contributionUUID -> indexingObserver.onContributionsChanged(issue.getRepo().getId(), contributionUUID));

        issueStorage.save(GithubIssue.of(issue));
    }

    private Contribution[] contributionsOf(CleanIssue issue) {
        if (issue.getAssignees().isEmpty())
            return new Contribution[]{Contribution.of(GithubIssue.of(issue))};

        return issue.getAssignees().stream()
                .map(assignee -> Contribution.of(GithubIssue.of(issue), GithubAccount.of(assignee)))
                .toArray(Contribution[]::new);
    }
}
