package com.onlydust.marketplace.indexer.postgres.repositories.exposition;

import com.onlydust.marketplace.indexer.postgres.entities.exposition.RepoContributorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface RepoContributorRepository extends JpaRepository<RepoContributorEntity, String> {
    @Modifying
    @Query(value = """
            INSERT INTO indexer_exp.repos_contributors (repo_id, contributor_id)
            SELECT DISTINCT repo_id, contributor_id from indexer_exp.contributions
            WHERE repo_id = :repoId
            """, nativeQuery = true)
    void insertAllByRepoId(Long repoId);

    @Modifying
    @Query(value = """
             DELETE FROM indexer_exp.repos_contributors
            WHERE repo_id = :repoId
            """, nativeQuery = true)
    void deleteAllByRepoId(Long repoId);
}
