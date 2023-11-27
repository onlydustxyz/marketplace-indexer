package com.onlydust.marketplace.indexer.postgres.repositories.exposition;

import com.onlydust.marketplace.indexer.postgres.entities.exposition.ContributionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContributionRepository extends JpaRepository<ContributionEntity, String> {
    void deleteAllByRepoIdAndGithubNumber(Long repoId, Long githubNumber);
}
