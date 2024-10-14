package com.onlydust.marketplace.indexer.postgres.repositories.raw;

import com.onlydust.marketplace.indexer.postgres.entities.raw.RawUserSocialAccountsEntity;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;

import java.util.List;

public interface UserSocialAccountsRepository extends BaseJpaRepository<RawUserSocialAccountsEntity, Long> {
    void deleteAll();

    List<RawUserSocialAccountsEntity> findAll();
}
