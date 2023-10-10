package com.onlydust.marketplace.indexer.domain.models.clean;

import com.onlydust.marketplace.indexer.domain.models.raw.RawSocialAccount;

import java.util.List;

public record User(Long id, String login, String type, String htmlUrl, String avatarUrl,
                   List<RawSocialAccount> socialAccounts) {
}
