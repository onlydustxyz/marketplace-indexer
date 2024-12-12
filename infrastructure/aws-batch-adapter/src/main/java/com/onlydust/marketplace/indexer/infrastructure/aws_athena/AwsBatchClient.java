package com.onlydust.marketplace.indexer.infrastructure.aws_athena;

import lombok.Data;
import software.amazon.awssdk.services.batch.BatchClient;
import software.amazon.awssdk.services.batch.model.EcsPropertiesOverride;
import software.amazon.awssdk.services.batch.model.SubmitJobRequest;
import software.amazon.awssdk.services.batch.model.TaskContainerOverrides;
import software.amazon.awssdk.services.batch.model.TaskPropertiesOverride;

public class AwsBatchClient {
    final BatchClient client;
    final Properties properties;

    public AwsBatchClient(final Properties properties) {
        this.client = BatchClient.create();
        this.properties = properties;
    }

    public void execute(String jobName, String... args) {
        client.submitJob(SubmitJobRequest.builder()
                .jobName(jobName)
                .jobDefinition(properties.jobDefinition)
                .jobQueue(properties.queue)
                .ecsPropertiesOverride(EcsPropertiesOverride.builder()
                        .taskProperties(TaskPropertiesOverride.builder()
                                .containers(TaskContainerOverrides.builder()
                                        .name(properties.containerName)
                                        .command(args)
                                        .build())
                                .build())
                        .build())
                .build());
    }

    @Data
    public static class Properties {
        String queue;
        String jobDefinition;
        String containerName;
    }
}
