package com.onlydust.marketplace.indexer.postgres.entities.exposition;

import com.onlydust.marketplace.indexer.domain.models.exposition.GithubRepo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.util.Date;
import java.util.List;

@Entity
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "github_repos", schema = "indexer_exp")
public class GithubRepoEntity {
    @Id
    Long id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    GithubAccountEntity owner;
    String name;
    String ownerLogin;
    String htmlUrl;
    Date updatedAt;
    String description;
    Long starsCount;
    Long forksCount;
    Boolean hasIssues;
    Date deletedAt;

    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true, fetch = FetchType.LAZY, mappedBy = "repoId")
    List<GithubRepoLanguageEntity> languages;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    GithubRepoEntity parent;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "github_repo_visibility")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    Visibility visibility;

    public static GithubRepoEntity of(GithubRepo repo) {
        return GithubRepoEntity.builder()
                .id(repo.getId())
                .owner(GithubAccountEntity.of(repo.getOwner()))
                .ownerLogin(repo.getOwner().getLogin())
                .name(repo.getName())
                .htmlUrl(repo.getHtmlUrl())
                .updatedAt(repo.getUpdatedAt())
                .description(repo.getDescription())
                .starsCount(repo.getStarsCount())
                .forksCount(repo.getForksCount())
                .hasIssues(repo.getHasIssues())
                .languages(repo.getLanguages().entrySet().stream()
                        .map(language -> GithubRepoLanguageEntity.builder()
                                .repoId(repo.getId())
                                .language(language.getKey())
                                .lineCount(language.getValue())
                                .build()).toList())
                .parent(repo.getParent() == null ? null : GithubRepoEntity.of(repo.getParent()))
                .visibility(Visibility.of(repo.getVisibility()))
                .build();
    }

    public GithubRepoEntity updateWith(GithubRepo updated) {
        this.languages.clear();
        this.languages.addAll(updated.getLanguages().entrySet().stream()
                .map(language -> GithubRepoLanguageEntity.builder()
                        .repoId(updated.getId())
                        .language(language.getKey())
                        .lineCount(language.getValue())
                        .build()).toList());

        return this.toBuilder()
                .name(updated.getName())
                .htmlUrl(updated.getHtmlUrl())
                .updatedAt(updated.getUpdatedAt())
                .description(updated.getDescription())
                .starsCount(updated.getStarsCount())
                .forksCount(updated.getForksCount())
                .hasIssues(updated.getHasIssues())
                .visibility(Visibility.of(updated.getVisibility()))
                .deletedAt(updated.getDeletedAt())
                .build();
    }

    public enum Visibility {
        PRIVATE, PUBLIC;

        public static GithubRepoEntity.Visibility of(GithubRepo.Visibility type) {
            return switch (type) {
                case PRIVATE -> PRIVATE;
                case PUBLIC -> PUBLIC;
            };
        }
    }
}
