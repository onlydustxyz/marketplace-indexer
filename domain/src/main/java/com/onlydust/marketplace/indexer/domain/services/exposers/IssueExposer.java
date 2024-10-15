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

@AllArgsConstructor
public class IssueExposer implements Exposer<CleanIssue> {
    private final ContributionStorage contributionStorage;
    private final IssueStorage issueStorage;
    private final IndexingObserver indexingObserver;

    @Override
    @Transactional
    public void expose(CleanIssue issue) {
        contributionStorage.deleteAllByRepoIdAndGithubNumber(issue.getRepo().getId(), issue.getNumber());
        
        if (issue.getAssignees().isEmpty())
            contributionStorage.saveAll(Contribution.of(GithubIssue.of(issue)));
        else
            contributionStorage.saveAll(issue.getAssignees().stream()
                    .map(assignee -> Contribution.of(GithubIssue.of(issue), GithubAccount.of(assignee)))
                    .toArray(Contribution[]::new));

        indexingObserver.onContributionsChanged(issue.getRepo().getId());
        issueStorage.save(GithubIssue.of(issue));
    }
}
