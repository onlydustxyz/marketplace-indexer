package com.onlydust.marketplace.indexer.domain.ports.in.jobs;

public interface JobExecutor {
    void execute(String... args);
}
