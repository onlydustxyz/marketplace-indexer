package com.onlydust.marketplace.indexer.domain.models.clean;

import com.onlydust.marketplace.indexer.domain.models.raw.RawSocialAccount;

import java.util.List;

public record User(Long id, String login, List<RawSocialAccount> socialAccounts) {
}
