package com.onlydust.marketplace.indexer.postgres.repositories.raw;

import com.onlydust.marketplace.indexer.postgres.entities.raw.RawPullRequestClosingIssuesEntity;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;

public interface PullRequestClosingIssueViewRepository extends BaseJpaRepository<RawPullRequestClosingIssuesEntity,
        RawPullRequestClosingIssuesEntity.PrimaryKey> {
}
