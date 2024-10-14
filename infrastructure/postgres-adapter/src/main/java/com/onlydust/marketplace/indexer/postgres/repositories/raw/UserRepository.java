package com.onlydust.marketplace.indexer.postgres.repositories.raw;

import com.onlydust.marketplace.indexer.postgres.entities.raw.RawUserEntity;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;

import java.util.List;

public interface UserRepository extends BaseJpaRepository<RawUserEntity, Long> {
    void deleteAll();

    List<RawUserEntity> findAll();
}
