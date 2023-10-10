package com.onlydust.marketplace.indexer.postgres.repositories.exposition;

import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GithubAccountRepository extends JpaRepository<GithubAccount, Long> {
}
