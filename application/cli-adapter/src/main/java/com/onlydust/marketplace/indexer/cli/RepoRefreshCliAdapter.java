package com.onlydust.marketplace.indexer.cli;

import com.onlydust.marketplace.indexer.domain.ports.in.jobs.JobManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;

@AllArgsConstructor
@Slf4j
@Profile("cli")
public class RepoRefreshCliAdapter implements CommandLineRunner {
    private final JobManager cacheOnlyRepoRefreshJobManager;

    @Override
    public void run(String... args) {
        if (args.length == 0 || !args[0].equals("refresh_repos")) return;
        
        LOGGER.info("Refreshing repos");
        cacheOnlyRepoRefreshJobManager.createJob().run();
    }
}
