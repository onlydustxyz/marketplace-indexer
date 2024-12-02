package com.onlydust.marketplace.indexer.infrastructure.aws_athena;

import lombok.Data;
import software.amazon.awssdk.services.batch.BatchClient;
import software.amazon.awssdk.services.batch.model.ContainerOverrides;
import software.amazon.awssdk.services.batch.model.SubmitJobRequest;

public class AwsBatchClient {
    final BatchClient client;
    final Properties properties;

    public AwsBatchClient(final Properties properties) {
        this.client = BatchClient.create();
        this.properties = properties;
    }

    public void execute(String... args) {
        client.submitJob(SubmitJobRequest.builder()
                .jobDefinition(properties.jobDefinition)
                .jobQueue(properties.queue)
                .containerOverrides(ContainerOverrides.builder()
                        .command(args)
                        .build())
                .build());
    }

    @Data
    public static class Properties {
        String queue;
        String jobDefinition;
    }
}
