package com.onlydust.marketplace.indexer.postgres.repositories.raw;

import com.onlydust.marketplace.indexer.postgres.entities.raw.RawCommitEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommitRepository extends JpaRepository<RawCommitEntity, String> {
}
