package com.onlydust.marketplace.indexer.postgres.entities.exposition;

import com.onlydust.marketplace.indexer.domain.models.exposition.GithubAccount;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.ZonedDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "github_accounts", schema = "indexer_exp")
public class GithubAccountEntity {
    @Id
    Long id;
    String login;
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "github_account_type")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    GithubAccountEntity.Type type;
    String htmlUrl;
    String avatarUrl;
    String name;
    String bio;
    String location;
    String website;
    String twitter;
    String linkedin;
    String telegram;
    ZonedDateTime createdAt;

    public static GithubAccountEntity of(GithubAccount account) {
        return GithubAccountEntity.builder()
                .id(account.getId())
                .login(account.getLogin())
                .type(Type.of(account.getType()))
                .htmlUrl(account.getHtmlUrl())
                .avatarUrl(account.getAvatarUrl())
                .name(account.getName())
                .bio(account.getBio())
                .location(account.getLocation())
                .website(account.getWebsite())
                .twitter(account.getTwitter())
                .linkedin(account.getLinkedin())
                .telegram(account.getTelegram())
                .createdAt(account.getCreatedAt())
                .build();
    }

    public enum Type {
        USER, ORGANIZATION, BOT;

        public static Type of(GithubAccount.Type type) {
            return switch (type) {
                case USER -> USER;
                case ORGANIZATION -> ORGANIZATION;
                case BOT -> BOT;
            };
        }
    }
}
