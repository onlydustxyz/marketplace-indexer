package com.onlydust.marketplace.indexer.domain.mappers;

import com.onlydust.marketplace.indexer.domain.models.clean.User;
import com.onlydust.marketplace.indexer.domain.models.raw.RawSocialAccount;
import com.onlydust.marketplace.indexer.domain.models.raw.RawUser;

import java.util.ArrayList;
import java.util.List;

public interface UserMapper {
    static User map(RawUser user) {
        return map(user, new ArrayList<>());
    }

    static User map(RawUser user, List<RawSocialAccount> socialAccounts) {
        return new User(user.getId(), user.getLogin(), user.getType(), user.getHtmlUrl(), user.getAvatarUrl(), socialAccounts);
    }
}
