package com.onlydust.marketplace.indexer.postgres.entities.exposition;

import com.onlydust.marketplace.indexer.domain.models.exposition.GithubAppInstallation;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "github_app_installations", schema = "indexer_exp")
public class GithubAppInstallationEntity {
    @Id
    Long id;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    GithubAccountEntity account;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "authorized_github_repos",
            schema = "indexer_exp",
            joinColumns = @JoinColumn(name = "installation_id"),
            inverseJoinColumns = @JoinColumn(name = "repo_id")
    )
    Set<GithubRepoEntity> repos;

    Date suspendedAt;

    public static GithubAppInstallationEntity of(GithubAppInstallation installation) {
        return GithubAppInstallationEntity.builder()
                .id(installation.getId())
                .account(GithubAccountEntity.of(installation.getAccount()))
                .repos(installation.getRepos().stream().map(GithubRepoEntity::of).collect(Collectors.toUnmodifiableSet()))
                .build();
    }
}
