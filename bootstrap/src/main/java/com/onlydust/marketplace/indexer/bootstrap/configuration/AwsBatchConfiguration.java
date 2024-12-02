package com.onlydust.marketplace.indexer.bootstrap.configuration;

import com.onlydust.marketplace.indexer.infrastructure.aws_athena.AwsBatchClient;
import com.onlydust.marketplace.indexer.infrastructure.aws_athena.adapters.AwsBatchJobExecutorAdapter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsBatchConfiguration {
    @Bean
    @ConfigurationProperties(value = "infrastructure.aws.batch")
    public AwsBatchClient.Properties awsBatchProperties() {
        return new AwsBatchClient.Properties();
    }

    @Bean
    public AwsBatchClient awsBatchClient(
            final AwsBatchClient.Properties awsBatchProperties) {
        return new AwsBatchClient(awsBatchProperties);
    }

    @Bean
    public AwsBatchJobExecutorAdapter awsBatchJobExecutorAdapter(final AwsBatchClient awsBatchClient) {
        return new AwsBatchJobExecutorAdapter(awsBatchClient);
    }
}
