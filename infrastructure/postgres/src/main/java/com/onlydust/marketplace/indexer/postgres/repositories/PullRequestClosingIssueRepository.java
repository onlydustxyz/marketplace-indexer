package com.onlydust.marketplace.indexer.postgres.repositories;

import com.onlydust.marketplace.indexer.postgres.entities.PullRequestClosingIssue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PullRequestClosingIssueRepository extends JpaRepository<PullRequestClosingIssue, PullRequestClosingIssue.Id> {
    List<PullRequestClosingIssue> findAllByPullRequestRepoOwnerAndPullRequestRepoNameAndPullRequestNumber(String repoOwner, String repoName, Long prNumber);
}
