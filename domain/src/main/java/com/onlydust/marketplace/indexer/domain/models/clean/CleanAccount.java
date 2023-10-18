package com.onlydust.marketplace.indexer.domain.models.clean;

import com.onlydust.marketplace.indexer.domain.models.raw.RawSocialAccount;
import com.onlydust.marketplace.indexer.domain.models.raw.RawAccount;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Builder(access = AccessLevel.PRIVATE, toBuilder = true)
@Value
public class CleanAccount {
    Long id;
    String login;
    String type;
    String htmlUrl;
    String avatarUrl;
    @Builder.Default
    List<RawSocialAccount> socialAccounts = new ArrayList<>();

    public static CleanAccount of(RawAccount account) {
        return CleanAccount.builder()
                .id(account.getId())
                .login(account.getLogin())
                .type(account.getType())
                .htmlUrl(account.getHtmlUrl())
                .avatarUrl(account.getAvatarUrl())
                .build();
    }


    public static CleanAccount of(RawAccount account, List<RawSocialAccount> socialAccounts) {
        return CleanAccount.of(account).toBuilder()
                .socialAccounts(socialAccounts)
                .build();
    }
}
