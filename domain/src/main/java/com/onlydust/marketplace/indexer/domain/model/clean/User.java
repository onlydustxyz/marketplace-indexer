package com.onlydust.marketplace.indexer.domain.model.clean;

import com.onlydust.marketplace.indexer.domain.model.raw.RawSocialAccount;

import java.util.List;

public record User(Integer id, String login, List<RawSocialAccount> socialAccounts) {
}
