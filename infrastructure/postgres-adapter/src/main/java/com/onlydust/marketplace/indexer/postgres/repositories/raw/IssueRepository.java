package com.onlydust.marketplace.indexer.postgres.repositories.raw;

import com.onlydust.marketplace.indexer.postgres.entities.raw.RawIssueEntity;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;

import java.util.List;
import java.util.Optional;

public interface IssueRepository extends BaseJpaRepository<RawIssueEntity, Long> {
    List<RawIssueEntity> findAllByRepoId(Long repoId);

    Optional<RawIssueEntity> findByRepoIdAndNumber(Long repoId, Long issueNumber);

    List<RawIssueEntity> findAll();
}
