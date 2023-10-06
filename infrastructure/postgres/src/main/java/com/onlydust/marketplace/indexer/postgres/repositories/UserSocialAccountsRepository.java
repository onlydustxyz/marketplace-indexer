package com.onlydust.marketplace.indexer.postgres.repositories;

import com.onlydust.marketplace.indexer.postgres.entities.UserSocialAccounts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSocialAccountsRepository extends JpaRepository<UserSocialAccounts, Long> {

}
