package com.onlydust.marketplace.indexer.postgres.repositories.raw;

import com.onlydust.marketplace.indexer.postgres.entities.raw.RepoLanguages;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepoLanguagesRepository extends JpaRepository<RepoLanguages, Long> {

}
