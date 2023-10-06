package com.onlydust.marketplace.indexer.postgres.entities;

import com.onlydust.marketplace.indexer.domain.models.raw.RawSocialAccount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.ZonedDateTime;
import java.util.List;


@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_social_accounts", schema = "indexer_raw")
public class UserSocialAccounts {
    @Id
    @Column(name = "user_id")
    Long userId;
    @Column(name = "data")
    @Type(type = "jsonb")
    List<RawSocialAccount> data;
    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    ZonedDateTime createdAt;
    @Column(name = "updated_at")
    @UpdateTimestamp
    ZonedDateTime updatedAt;

    public static UserSocialAccounts of(Long userId, List<RawSocialAccount> socialAccounts) {
        return UserSocialAccounts.builder().userId(userId).data(socialAccounts).build();
    }
}
