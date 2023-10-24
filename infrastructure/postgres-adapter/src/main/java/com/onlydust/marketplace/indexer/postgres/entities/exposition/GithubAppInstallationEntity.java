package com.onlydust.marketplace.indexer.postgres.entities.exposition;

import com.onlydust.marketplace.indexer.domain.models.exposition.GithubAppInstallation;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "github_app_installations", schema = "indexer_exp")
public class GithubAppInstallationEntity {
    @Id
    Long id;
    @OneToOne
    GithubAccountEntity account;

    public static GithubAppInstallationEntity of(GithubAppInstallation installation) {
        return GithubAppInstallationEntity.builder()
                .id(installation.getId())
                .account(GithubAccountEntity.of(installation.getAccount()))
                .build();
    }
}
