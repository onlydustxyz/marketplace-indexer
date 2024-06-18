package com.onlydust.marketplace.indexer.postgres.entities.exposition;

import com.onlydust.marketplace.indexer.domain.models.exposition.GithubAppInstallation;
import io.hypersistence.utils.hibernate.type.array.StringArrayType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Accessors(chain = true, fluent = true)
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
    List<GithubRepoEntity> repos;

    Date suspendedAt;

    @NonNull
    @Type(StringArrayType.class)
    String[] permissions;

    public static GithubAppInstallationEntity of(GithubAppInstallation installation) {
        return GithubAppInstallationEntity.builder()
                .id(installation.getId())
                .account(GithubAccountEntity.of(installation.getAccount()))
                .repos(installation.getRepos().stream().map(GithubRepoEntity::of).toList())
                .permissions(installation.getPermissions().toArray(String[]::new))
                .build();
    }

    public GithubAppInstallationEntity permissions(Set<String> permissions) {
        this.permissions = permissions.toArray(String[]::new);
        return this;
    }
}
