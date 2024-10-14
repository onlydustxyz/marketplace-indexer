package com.onlydust.marketplace.indexer.postgres.repositories.raw;

import com.onlydust.marketplace.indexer.postgres.entities.raw.RawPullRequestClosingIssuesEntity;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;

import java.util.List;

public interface PullRequestClosingIssueRepository extends BaseJpaRepository<RawPullRequestClosingIssuesEntity, RawPullRequestClosingIssuesEntity.PrimaryKey> {
    List<RawPullRequestClosingIssuesEntity> findAll();
}
