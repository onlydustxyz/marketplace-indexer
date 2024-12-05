package com.onlydust.marketplace.indexer.domain.ports.in.jobs;

public interface JobExecutor {
    void execute(String jobName, String... args);
}
