package com.onlydust.marketplace.indexer.postgres.repositories;

import com.onlydust.marketplace.indexer.postgres.entities.Repo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RepoRepository extends JpaRepository<Repo, Long> {
    Optional<Repo> findByOwnerAndName(String repoOwner, String repoName);
}
