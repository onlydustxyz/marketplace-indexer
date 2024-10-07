package com.onlydust.marketplace.indexer.postgres.repositories.exposition;

import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubUserFileExtensionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface GithubUserFileExtensionsRepository extends JpaRepository<GithubUserFileExtensionEntity, GithubUserFileExtensionEntity.PrimaryKey> {
    @Modifying
    @Query(value = """
            insert into indexer_exp.user_file_extensions(user_id, file_extension, commit_count)
            select :userId, unnest(:fileExtensions), 1
            on conflict (user_id, file_extension) do update
            set commit_count = commit_count + 1
            where user_id = :userId
            """, nativeQuery = true)
    void addCommitForUserAndExtensions(Long userId, String[] fileExtensions);
}
