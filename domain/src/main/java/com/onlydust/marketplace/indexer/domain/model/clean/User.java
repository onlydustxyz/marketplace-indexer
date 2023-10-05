package com.onlydust.marketplace.indexer.domain.model.clean;

import com.onlydust.marketplace.indexer.domain.model.raw.RawSocialAccount;

import java.util.List;

public record User(Long id, String login, List<RawSocialAccount> socialAccounts) {
}
