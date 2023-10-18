package com.onlydust.marketplace.indexer.postgres.entities.exposition;

import com.onlydust.marketplace.indexer.domain.models.exposition.GithubAccount;
import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;

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
    @Type(type = "github_account_type")
    GithubAccountType type;
    String htmlUrl;
    String avatarUrl;
    Long installationId;

    public static GithubAccountEntity of(GithubAccount account) {
        return GithubAccountEntity.builder()
                .id(account.getId())
                .login(account.getLogin())
                .type(GithubAccountType.of(account.getType()))
                .htmlUrl(account.getHtmlUrl())
                .avatarUrl(account.getAvatarUrl())
                .installationId(account.getInstallationId())
                .build();
    }

    public enum GithubAccountType {
        USER,
        ORGANIZATION;

        public static GithubAccountType of(GithubAccount.Type type) {
            return switch (type) {
                case USER -> USER;
                case ORGANIZATION -> ORGANIZATION;
            };
        }
    }
}
