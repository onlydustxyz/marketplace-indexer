package com.onlydust.marketplace.indexer.infrastructure.aws_athena.adapters;

import com.onlydust.marketplace.indexer.domain.ports.in.jobs.JobExecutor;
import com.onlydust.marketplace.indexer.infrastructure.aws_athena.AwsBatchClient;
import lombok.AllArgsConstructor;

import java.util.stream.Stream;

@AllArgsConstructor
public class AwsBatchJobExecutorAdapter implements JobExecutor {
    private final AwsBatchClient client;

    @Override
    public void execute(String jobName, String... args) {
        client.execute(jobName, Stream.concat(Stream.of(jobName), Stream.of(args)).toArray(String[]::new));
    }
}
