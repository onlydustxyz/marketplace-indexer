package com.onlydust.marketplace.indexer.postgres.repositories.raw;

import com.onlydust.marketplace.indexer.postgres.entities.raw.RawRepoLanguagesEntity;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;

public interface RepoLanguagesRepository extends BaseJpaRepository<RawRepoLanguagesEntity, Long> {

}
