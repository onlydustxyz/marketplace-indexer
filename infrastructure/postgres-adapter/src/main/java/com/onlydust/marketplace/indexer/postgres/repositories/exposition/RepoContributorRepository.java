package com.onlydust.marketplace.indexer.postgres.repositories.exposition;

import com.onlydust.marketplace.indexer.postgres.entities.exposition.RepoContributorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface RepoContributorRepository extends JpaRepository<RepoContributorEntity, String> {
    @Modifying
    @Query(value = """
            INSERT INTO indexer_exp.repos_contributors (repo_id, contributor_id, has_completed_contribution)
            SELECT DISTINCT ON (c.repo_id, c.contributor_id)
              c.repo_id,
              c.contributor_id,
              max(case when c.status = 'COMPLETED' then 1 else 0 end) = 1
            FROM indexer_exp.contributions c
            WHERE c.repo_id = :repoId
            GROUP BY (c.repo_id, c.contributor_id)
            """, nativeQuery = true)
    void insertAllByRepoId(Long repoId);

    @Modifying
    @Query(value = """
             DELETE FROM indexer_exp.repos_contributors
            WHERE repo_id = :repoId
            """, nativeQuery = true)
    void deleteAllByRepoId(Long repoId);
}
