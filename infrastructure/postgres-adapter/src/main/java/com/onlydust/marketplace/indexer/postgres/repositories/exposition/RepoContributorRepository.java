package com.onlydust.marketplace.indexer.postgres.repositories.exposition;

import com.onlydust.marketplace.indexer.postgres.entities.exposition.RepoContributorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface RepoContributorRepository extends JpaRepository<RepoContributorEntity, String> {
    @Modifying
    @Query(value = """
            INSERT INTO indexer_exp.repos_contributors (repo_id, contributor_id, completed_contribution_count, total_contribution_count)
            SELECT DISTINCT ON (c.repo_id, c.contributor_id)
              c.repo_id,
              c.contributor_id,
              count(c.id) filter ( where c.status = 'COMPLETED' ),
              count(c.id)
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
