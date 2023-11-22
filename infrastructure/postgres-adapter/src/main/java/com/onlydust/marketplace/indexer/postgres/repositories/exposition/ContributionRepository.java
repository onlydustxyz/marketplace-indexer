package com.onlydust.marketplace.indexer.postgres.repositories.exposition;

import com.onlydust.marketplace.indexer.postgres.entities.exposition.ContributionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Set;

public interface ContributionRepository extends JpaRepository<ContributionEntity, String> {
    @Query("select distinct c.repo.id from ContributionEntity c where c.techUpdatedAt >= :since")
    Set<Long> listReposWithContributionsUpdatedSince(Instant since);
}
