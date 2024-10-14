package com.onlydust.marketplace.indexer.cli;

import com.onlydust.marketplace.indexer.domain.ports.in.jobs.JobManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;

@AllArgsConstructor
@Slf4j
@Profile("cli")
public class CommitRefreshCliAdapter implements CommandLineRunner {
    private final JobManager cacheOnlyCommitRefreshJobManager;

    @Override
    public void run(String... args) {
        if (args.length == 0 || !args[0].equals("refresh_commits")) return;

        LOGGER.info("Refreshing commits");
        cacheOnlyCommitRefreshJobManager.createJob().run();
    }
}
