package com.onlydust.marketplace.indexer.cli;

import com.onlydust.marketplace.indexer.domain.ports.in.jobs.JobManager;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;

@Controller("refresh_commits")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommitRefreshCliAdapter implements Batch {
    JobManager cacheOnlyCommitRefreshJobManager;

    @Override
    public void run(String... args) {
        cacheOnlyCommitRefreshJobManager.createJob().run();
    }
}
