package com.onlydust.marketplace.indexer.postgres.entities.exposition;

import com.onlydust.marketplace.indexer.domain.models.clean.User;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "github_accounts", schema = "indexer_exp")
public class GithubAccount {
    @Id
    Long id;
    String login;
    String type;
    String htmlUrl;
    String avatarUrl;
    Long installationId;

    public static GithubAccount of(Long installationId, User account) {
        return GithubAccount.builder()
                .id(account.id())
                .login(account.login())
                .type(account.type())
                .htmlUrl(account.htmlUrl())
                .avatarUrl(account.avatarUrl())
                .installationId(installationId)
                .build();
    }
}
