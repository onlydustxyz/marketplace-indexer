package com.onlydust.marketplace.indexer.postgres.entities.exposition;

import com.onlydust.marketplace.indexer.domain.models.exposition.GithubLabel;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Data
@Builder(access = AccessLevel.PRIVATE, toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "github_labels", schema = "indexer_exp")
public class GithubLabelEntity {
    @Id
    @EqualsAndHashCode.Include
    @NonNull
    Long id;

    @NonNull
    String name;
    String description;

    public static GithubLabelEntity of(GithubLabel label) {
        return GithubLabelEntity.builder()
                .id(label.getId())
                .name(label.getName())
                .description(label.getDescription())
                .build();
    }
}
