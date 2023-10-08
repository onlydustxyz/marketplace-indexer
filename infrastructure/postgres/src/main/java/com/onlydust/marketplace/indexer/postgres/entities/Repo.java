package com.onlydust.marketplace.indexer.postgres.entities;

import com.onlydust.marketplace.indexer.domain.models.raw.RawRepo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.ZonedDateTime;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "repos", schema = "indexer_raw")
public class Repo {
    @Id
    Long id;
    String owner;
    String name;
    @Type(type = "jsonb")
    RawRepo data;
    @CreationTimestamp
    ZonedDateTime createdAt;
    @UpdateTimestamp
    ZonedDateTime updatedAt;

    public static Repo of(RawRepo repo) {
        return Repo.builder().id(repo.getId()).owner(repo.getOwner().getLogin()).name(repo.getName()).data(repo).build();
    }
}
