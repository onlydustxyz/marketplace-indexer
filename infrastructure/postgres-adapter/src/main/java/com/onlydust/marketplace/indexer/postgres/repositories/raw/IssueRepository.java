package com.onlydust.marketplace.indexer.postgres.repositories.raw;

import com.onlydust.marketplace.indexer.postgres.entities.raw.RawIssueEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IssueRepository extends JpaRepository<RawIssueEntity, Long> {
    List<RawIssueEntity> findAllByRepoId(Long repoId);

    Optional<RawIssueEntity> findByRepoIdAndNumber(Long repoId, Long issueNumber);
}
