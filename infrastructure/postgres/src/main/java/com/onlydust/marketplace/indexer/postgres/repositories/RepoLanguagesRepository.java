package com.onlydust.marketplace.indexer.postgres.repositories;

import com.onlydust.marketplace.indexer.postgres.entities.RepoLanguages;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepoLanguagesRepository extends JpaRepository<RepoLanguages, Long> {

}
