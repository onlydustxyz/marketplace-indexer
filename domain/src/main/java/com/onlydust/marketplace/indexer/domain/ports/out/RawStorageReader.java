package com.onlydust.marketplace.indexer.domain.ports.out;

import com.onlydust.marketplace.indexer.domain.model.SocialAccount;
import com.onlydust.marketplace.indexer.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface RawStorageReader {
    Optional<User> userById(Integer userId);

    Optional<List<SocialAccount>> userSocialAccountsById(Integer userId);
}
