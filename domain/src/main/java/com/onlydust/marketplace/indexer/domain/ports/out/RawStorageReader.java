package com.onlydust.marketplace.indexer.domain.ports.out;

import com.onlydust.marketplace.indexer.domain.model.raw.RawSocialAccount;
import com.onlydust.marketplace.indexer.domain.model.raw.RawUser;

import java.util.List;
import java.util.Optional;

public interface RawStorageReader {
    Optional<RawUser> userById(Integer userId);

    Optional<List<RawSocialAccount>> userSocialAccountsById(Integer userId);
}
