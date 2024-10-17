package com.onlydust.marketplace.indexer.postgres.repositories.exposition;

import com.onlydust.marketplace.indexer.postgres.entities.exposition.GroupedContributionEntity;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface GroupedContributionRepository extends BaseJpaRepository<GroupedContributionEntity, UUID>,
        ListPagingAndSortingRepository<GroupedContributionEntity, UUID> {

    void deleteAllByRepoIdAndGithubNumber(Long repoId, Long githubNumber);

    List<GroupedContributionEntity> findAll();
}
