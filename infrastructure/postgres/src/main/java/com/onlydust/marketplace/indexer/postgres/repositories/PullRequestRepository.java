package com.onlydust.marketplace.indexer.postgres.repositories;

import com.onlydust.marketplace.indexer.postgres.entities.PullRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PullRequestRepository extends JpaRepository<PullRequest, Long> {
    List<PullRequest> findAllByRepoId(Long repoId);

    Optional<PullRequest> findByRepoIdAndNumber(Long repoId, Long prNumber);
}
