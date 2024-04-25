package com.onlydust.marketplace.indexer.postgres.repositories.raw;

import com.onlydust.marketplace.indexer.postgres.entities.raw.RawUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<RawUserEntity, Long> {

}
