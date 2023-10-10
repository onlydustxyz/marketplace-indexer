package com.onlydust.marketplace.indexer.postgres.repositories.raw;

import com.onlydust.marketplace.indexer.postgres.entities.raw.UserSocialAccounts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSocialAccountsRepository extends JpaRepository<UserSocialAccounts, Long> {

}
