package com.onlydust.marketplace.indexer.postgres.repositories.exposition;

import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubIssueAssigneeEntity;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;

import java.util.List;

public interface GithubIssueAssigneeRepository extends BaseJpaRepository<GithubIssueAssigneeEntity, Long> {
    void deleteByIssueIdAndUserId(Long issueId, Long assigneeId);

    List<GithubIssueAssigneeEntity> findAllByIssueId(Long issueId);

    void deleteByIssueId(Long issueId);
}
