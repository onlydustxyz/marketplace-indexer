package com.onlydust.marketplace.indexer.postgres.entities;

import com.onlydust.marketplace.indexer.domain.models.raw.RawUser;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;


@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users", schema = "indexer_raw")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class User {
    @Id
    Long id;

    String login;

    @Type(type = "jsonb")
    RawUser data;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    Instant updatedAt;

    public static User of(RawUser user) {
        return User.builder().id(user.getId()).login(user.getLogin()).data(user).build();
    }
}
