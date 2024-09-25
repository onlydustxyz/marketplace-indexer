package com.onlydust.marketplace.indexer.bootstrap.configuration;

import com.onlydust.marketplace.indexer.bigquery.BigQueryClient;
import com.onlydust.marketplace.indexer.bigquery.adapters.BigQueryPublicEventRawStorageReaderAdapter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BigQueryConfiguration {
    @Bean
    @ConfigurationProperties(value = "infrastructure.bigquery")
    public BigQueryClient.Properties bigQueryProperties() {
        return new BigQueryClient.Properties();
    }

    @Bean
    public BigQueryClient bigQueryClient(final BigQueryClient.Properties bigQueryProperties) {
        return new BigQueryClient(bigQueryProperties);
    }

    @Bean
    public BigQueryPublicEventRawStorageReaderAdapter bigQueryPublicEventRawStorageReaderAdapter(final BigQueryClient bigQueryClient) {
        return new BigQueryPublicEventRawStorageReaderAdapter(bigQueryClient);
    }
}
