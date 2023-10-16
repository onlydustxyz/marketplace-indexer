package com.onlydust.marketplace.indexer.postgres.repositories.raw;

import com.onlydust.marketplace.indexer.postgres.entities.raw.PullRequestClosingIssue;
import com.onlydust.marketplace.indexer.postgres.entities.raw.PullRequestClosingIssueView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PullRequestClosingIssueViewRepository extends JpaRepository<PullRequestClosingIssueView, PullRequestClosingIssue.Id> {
    List<PullRequestClosingIssueView> findAllByPullRequestRepoOwnerAndPullRequestRepoNameAndPullRequestNumber(String repoOwner, String repoName, Long prNumber);
}
