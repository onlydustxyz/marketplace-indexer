package com.onlydust.marketplace.indexer.domain.models.exposition;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanAccount;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(access = AccessLevel.PRIVATE, toBuilder = true)
public class GithubAccount {
    Long id;
    String login;
    Type type;
    String htmlUrl;
    String avatarUrl;
    String name;

    public static GithubAccount of(CleanAccount account) {
        return GithubAccount.builder()
                .id(account.getId())
                .login(account.getLogin())
                .type(Type.valueOf(account.getType().toUpperCase()))
                .htmlUrl(account.getHtmlUrl())
                .avatarUrl(account.getAvatarUrl())
                .name(account.getName())
                .build();
    }

    public enum Type {
        USER, ORGANIZATION, BOT
    }
}
