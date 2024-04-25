package com.onlydust.marketplace.indexer.postgres.repositories.raw;

import com.onlydust.marketplace.indexer.postgres.entities.raw.RawRepoLanguagesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepoLanguagesRepository extends JpaRepository<RawRepoLanguagesEntity, Long> {

}
