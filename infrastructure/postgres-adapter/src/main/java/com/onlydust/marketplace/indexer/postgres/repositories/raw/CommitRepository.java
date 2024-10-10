package com.onlydust.marketplace.indexer.postgres.repositories.raw;

import com.onlydust.marketplace.indexer.postgres.entities.raw.RawCommitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommitRepository extends JpaRepository<RawCommitEntity, String> {
    @Query(value = """
            with missing_commits_per_user as (select author_name,
                                                     count(sha) as commit_count
                                              from indexer_raw.commits
                                              where data is null
                                              group by author_name)
            select c.*
            from indexer_raw.commits c
                     join missing_commits_per_user mcpu on c.author_name = mcpu.author_name
            order by mcpu.commit_count desc
            limit :limit
            """, nativeQuery = true)
    List<RawCommitEntity> findAllForLeastIndexedUsers(int limit);
}
