package com.onlydust.marketplace.indexer.postgres.entities.exposition;

import com.onlydust.marketplace.indexer.domain.models.exposition.GithubLabel;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Builder(access = AccessLevel.PRIVATE, toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Getter
@Table(name = "github_labels", schema = "indexer_exp")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GithubLabelEntity {
    @Id
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
