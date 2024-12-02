package com.onlydust.marketplace.indexer.infrastructure.aws_athena.adapters;

import com.onlydust.marketplace.indexer.domain.ports.in.jobs.JobExecutor;
import com.onlydust.marketplace.indexer.infrastructure.aws_athena.AwsBatchClient;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AwsBatchJobExecutorAdapter implements JobExecutor {
    private final AwsBatchClient client;

    @Override
    public void execute(String... args) {
        client.execute(args);
    }
}
