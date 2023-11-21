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
    String bio;
    String location;
    String website;
    String twitter;
    String linkedin;
    String telegram;

    public static GithubAccount of(CleanAccount account) {
        return GithubAccount.builder()
                .id(account.getId())
                .login(account.getLogin())
                .type(Type.valueOf(account.getType().toUpperCase()))
                .htmlUrl(account.getHtmlUrl())
                .avatarUrl(account.getAvatarUrl())
                .name(account.getName())
                .bio(account.getBio())
                .location(account.getLocation())
                .website(account.getWebsite())
                .twitter(account.getTwitter())
                .linkedin(account.getLinkedin())
                .telegram(account.getTelegram())
                .build();
    }

    public enum Type {
        USER, ORGANIZATION, BOT
    }
}
