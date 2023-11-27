package com.onlydust.marketplace.indexer.postgres.entities.exposition;

import com.onlydust.marketplace.indexer.domain.models.exposition.GithubRepo;
import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "github_repos", schema = "indexer_exp")
@TypeDefs({
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class),
        @TypeDef(name = "github_repo_visibility", typeClass = PostgreSQLEnumType.class)
})
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

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    Map<String, Long> languages;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    GithubRepoEntity parent;

    @Enumerated(EnumType.STRING)
    @Type(type = "github_repo_visibility")
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
                .languages(repo.getLanguages())
                .parent(repo.getParent() == null ? null : GithubRepoEntity.of(repo.getParent()))
                .visibility(Visibility.of(repo.getVisibility()))
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
