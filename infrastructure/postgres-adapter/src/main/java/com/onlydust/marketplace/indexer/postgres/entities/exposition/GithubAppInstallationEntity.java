package com.onlydust.marketplace.indexer.postgres.entities.exposition;

import com.onlydust.marketplace.indexer.domain.models.exposition.GithubAppInstallation;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubRepo;
import io.hypersistence.utils.hibernate.type.array.StringArrayType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Type;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

@Entity
@Builder(access = AccessLevel.PRIVATE, toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Getter
@Accessors(chain = true, fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Table(name = "github_app_installations", schema = "indexer_exp")
public class GithubAppInstallationEntity {
    @Id
    final Long id;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    final GithubAccountEntity account;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "authorized_github_repos",
            schema = "indexer_exp",
            joinColumns = @JoinColumn(name = "installation_id"),
            inverseJoinColumns = @JoinColumn(name = "repo_id")
    )
    @Builder.Default
    final Set<GithubRepoEntity> repos = new HashSet<>();

    Date suspendedAt;

    @NonNull
    @Type(StringArrayType.class)
    String[] permissions;

    public static GithubAppInstallationEntity of(GithubAppInstallation installation) {
        return GithubAppInstallationEntity.builder()
                .id(installation.getId())
                .account(GithubAccountEntity.of(installation.getAccount()))
                .repos(installation.getRepos().stream().map(GithubRepoEntity::of).collect(toSet()))
                .permissions(installation.getPermissions().toArray(String[]::new))
                .build();
    }

    public GithubAppInstallationEntity permissions(Set<String> permissions) {
        this.permissions = permissions.toArray(String[]::new);
        return this;
    }

    public GithubAppInstallationEntity withAddedRepos(List<GithubRepo> repos) {
        return toBuilder()
                .repos(Stream.concat(this.repos.stream(), repos.stream().map(GithubRepoEntity::of)).collect(toSet()))
                .build();
    }
}
