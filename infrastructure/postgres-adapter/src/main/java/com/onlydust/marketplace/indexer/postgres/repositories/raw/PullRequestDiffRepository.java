package com.onlydust.marketplace.indexer.postgres.repositories.raw;

import com.onlydust.marketplace.indexer.postgres.entities.raw.RawPullRequestDiffEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PullRequestDiffRepository extends JpaRepository<RawPullRequestDiffEntity, Long> {

}
