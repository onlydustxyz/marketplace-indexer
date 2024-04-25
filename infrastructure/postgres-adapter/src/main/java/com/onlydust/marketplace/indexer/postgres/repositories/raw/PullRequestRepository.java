package com.onlydust.marketplace.indexer.postgres.repositories.raw;

import com.onlydust.marketplace.indexer.postgres.entities.raw.RawPullRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PullRequestRepository extends JpaRepository<RawPullRequestEntity, Long> {
    List<RawPullRequestEntity> findAllByRepoId(Long repoId);

    Optional<RawPullRequestEntity> findByRepoIdAndNumber(Long repoId, Long prNumber);
}
