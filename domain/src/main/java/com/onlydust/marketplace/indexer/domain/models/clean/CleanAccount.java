package com.onlydust.marketplace.indexer.domain.models.clean;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.onlydust.marketplace.indexer.domain.models.raw.RawAccount;
import com.onlydust.marketplace.indexer.domain.models.raw.RawShortAccount;
import com.onlydust.marketplace.indexer.domain.models.raw.RawSocialAccount;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

@Builder(access = AccessLevel.PRIVATE, toBuilder = true)
@Value
public class CleanAccount {
    Long id;
    String login;
    String type;
    String htmlUrl;
    String avatarUrl;
    String name;
    String bio;
    String location;
    String website;
    ZonedDateTime createdAt;
    Integer followerCount;
    @Builder.Default
    List<RawSocialAccount> socialAccounts = new ArrayList<>();

    public static CleanAccount of(RawAccount account) {
        return CleanAccount.builder()
                .id(account.getId())
                .login(account.getLogin())
                .type(account.getType())
                .htmlUrl(account.getHtmlUrl())
                .avatarUrl(account.getAvatarUrl())
                .name(account.getName())
                .bio(account.getBio())
                .location(account.getLocation())
                .website(account.getBlog())
                .createdAt(account.getCreatedAt() == null ? null : ZonedDateTime.parse(account.getCreatedAt()))
                .followerCount(account.getFollowers())
                .build();
    }

    public static CleanAccount of(RawShortAccount account) {
        return CleanAccount.builder()
                .id(account.getId())
                .login(account.getLogin())
                .type(account.getType())
                .htmlUrl(account.getHtmlUrl())
                .avatarUrl(account.getAvatarUrl())
                .followerCount(0)
                .build();
    }

    public static CleanAccount of(RawAccount account, List<RawSocialAccount> socialAccounts) {
        return CleanAccount.of(account).toBuilder()
                .socialAccounts(socialAccounts)
                .build();
    }

    public String getTwitter() {
        return socialAccounts.stream()
                .filter(socialAccount -> socialAccount.getProvider().equals("twitter"))
                .findFirst()
                .map(RawSocialAccount::getUrl)
                .orElse(null);
    }

    public String getLinkedin() {
        return socialAccounts.stream()
                .filter(socialAccount -> socialAccount.getProvider().equals("linkedin"))
                .findFirst()
                .map(RawSocialAccount::getUrl)
                .orElse(null);
    }

    public String getTelegram() {
        return socialAccounts.stream()
                .filter(socialAccount -> socialAccount.getProvider().equals("generic") &&
                                         (socialAccount.getUrl().startsWith("https://t.me") || socialAccount.getUrl().startsWith("https://telegram.me")))
                .findFirst()
                .map(RawSocialAccount::getUrl)
                .orElse(null);
    }
}
