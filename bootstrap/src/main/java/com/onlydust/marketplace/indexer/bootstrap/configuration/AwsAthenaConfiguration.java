package com.onlydust.marketplace.indexer.bootstrap.configuration;

import com.onlydust.marketplace.indexer.infrastructure.aws_athena.AwsAthenaClient;
import com.onlydust.marketplace.indexer.infrastructure.aws_athena.adapters.AwsAthenaPublicEventRawStorageReaderAdapter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;

@Configuration
public class AwsAthenaConfiguration {
    @Bean
    @ConfigurationProperties(value = "infrastructure.aws.athena")
    public AwsAthenaClient.Properties awsAthenaProperties() {
        return new AwsAthenaClient.Properties();
    }

    @Bean
    public AwsAthenaClient awsAthenaClient(
            final AwsAthenaClient.Properties awsAthenaProperties) {
        return new AwsAthenaClient(Executors.newSingleThreadScheduledExecutor(), awsAthenaProperties);
    }

    @Bean
    public AwsAthenaPublicEventRawStorageReaderAdapter awsAthenaPublicEventRawStorageReaderAdapter(final AwsAthenaClient awsAthenaClient) {
        return new AwsAthenaPublicEventRawStorageReaderAdapter(awsAthenaClient);
    }
}
