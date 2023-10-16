package com.onlydust.marketplace.indexer.postgres.entities.exposition;

import com.onlydust.marketplace.indexer.domain.models.clean.Repo;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "github_repos", schema = "indexer_exp")
public class GithubRepo {
    @Id
    Long id;

    @OneToOne
    GithubAccount owner;
    String name;
    String htmlUrl;
    Date updatedAt;
    String description;
    Long starsCount;
    Long forksCount;

    public static GithubRepo of(Long ownerId, Repo repo) {
        return GithubRepo.builder()
                .id(repo.id())
                .owner(GithubAccount.builder().id(ownerId).build())
                .name(repo.name())
                .htmlUrl(repo.htmlUrl())
                .updatedAt(repo.updatedAt())
                .description(repo.description())
                .starsCount(repo.starsCount())
                .forksCount(repo.forksCount())
                .build();
    }
}
