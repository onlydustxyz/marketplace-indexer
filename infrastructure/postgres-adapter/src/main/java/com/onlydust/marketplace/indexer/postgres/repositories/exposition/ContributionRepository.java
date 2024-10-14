package com.onlydust.marketplace.indexer.postgres.repositories.exposition;

import com.onlydust.marketplace.indexer.postgres.entities.exposition.ContributionEntity;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

import java.util.List;

public interface ContributionRepository extends BaseJpaRepository<ContributionEntity, String>,
        ListPagingAndSortingRepository<ContributionEntity, String> {

    void deleteAllByRepoIdAndGithubNumber(Long repoId, Long githubNumber);

    List<ContributionEntity> findAll();
}
