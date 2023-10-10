package com.onlydust.marketplace.indexer.postgres.repositories.raw;

import com.onlydust.marketplace.indexer.postgres.entities.raw.PullRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PullRequestRepository extends JpaRepository<PullRequest, Long> {
    List<PullRequest> findAllByRepoId(Long repoId);

    Optional<PullRequest> findByRepoIdAndNumber(Long repoId, Long prNumber);
}
