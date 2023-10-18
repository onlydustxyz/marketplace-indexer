package com.onlydust.marketplace.indexer.postgres.entities.exposition;

import com.onlydust.marketplace.indexer.domain.models.exposition.GithubRepo;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "github_repos", schema = "indexer_exp")
public class GithubRepoEntity {
    @Id
    Long id;

    @ManyToOne
    GithubAccountEntity owner;
    String name;
    String htmlUrl;
    Date updatedAt;
    String description;
    Long starsCount;
    Long forksCount;

    public static GithubRepoEntity of(GithubRepo repo) {
        return GithubRepoEntity.builder()
                .id(repo.getId())
                .owner(GithubAccountEntity.of(repo.getOwner()))
                .name(repo.getName())
                .htmlUrl(repo.getHtmlUrl())
                .updatedAt(repo.getUpdatedAt())
                .description(repo.getDescription())
                .starsCount(repo.getStarsCount())
                .forksCount(repo.getForksCount())
                .build();
    }
}
