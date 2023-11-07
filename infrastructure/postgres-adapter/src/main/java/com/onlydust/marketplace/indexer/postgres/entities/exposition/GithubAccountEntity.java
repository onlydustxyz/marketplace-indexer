package com.onlydust.marketplace.indexer.postgres.entities.exposition;

import com.onlydust.marketplace.indexer.domain.models.exposition.GithubAccount;
import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "github_accounts", schema = "indexer_exp")
@TypeDef(name = "github_account_type", typeClass = PostgreSQLEnumType.class)
public class GithubAccountEntity {
    @Id
    Long id;
    String login;
    @Enumerated(EnumType.STRING)
    @org.hibernate.annotations.Type(type = "github_account_type")
    GithubAccountEntity.Type type;
    String htmlUrl;
    String avatarUrl;
    String name;

    @EqualsAndHashCode.Exclude
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    Instant techCreatedAt;

    @EqualsAndHashCode.Exclude
    @UpdateTimestamp
    @Column(nullable = false)
    Instant techUpdatedAt;

    public static GithubAccountEntity of(GithubAccount account) {
        return GithubAccountEntity.builder()
                .id(account.getId())
                .login(account.getLogin())
                .type(Type.of(account.getType()))
                .htmlUrl(account.getHtmlUrl())
                .avatarUrl(account.getAvatarUrl())
                .name(account.getName())
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
