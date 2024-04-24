package com.onlydust.marketplace.indexer.postgres.repositories.raw;

import com.onlydust.marketplace.indexer.postgres.entities.raw.RawUserSocialAccountsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSocialAccountsRepository extends JpaRepository<RawUserSocialAccountsEntity, Long> {

}
