package com.onlydust.marketplace.indexer.postgres.repositories.exposition;

import com.onlydust.marketplace.indexer.postgres.entities.exposition.ContributionNotificationEntity;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;

public interface ContributionNotificationEntityRepository extends BaseJpaRepository<ContributionNotificationEntity, Long> {
    @Query(value = """
            select 
                1 as id,
                jsonb_agg(repo_id) as repo_ids, 
                max(tech_updated_at)  as last_updated_at
            from
                indexer_exp.contributions 
            where 
                tech_updated_at > :since
            """, nativeQuery = true)
    ContributionNotificationEntity listReposWithContributionsUpdatedSince(Instant since);
}
