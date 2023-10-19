package com.onlydust.marketplace.indexer.postgres.repositories.exposition;

import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface GithubAccountEntityRepository extends JpaRepository<GithubAccountEntity, Long> {
    @Modifying
    @Query(value = "UPDATE GithubAccountEntity set installationId = null where id = ?1")
    void removeInstallation(Long id);
}
