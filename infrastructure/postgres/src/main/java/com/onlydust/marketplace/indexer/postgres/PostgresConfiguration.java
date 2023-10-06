package com.onlydust.marketplace.indexer.postgres;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableAutoConfiguration
@EntityScan(basePackages = {
        "com.onlydust.marketplace.indexer.postgres.entities"
})
@EnableJpaRepositories(basePackages = {
        "com.onlydust.marketplace.indexer.postgres.repositories"
})
@EnableTransactionManagement
@EnableJpaAuditing
public class PostgresConfiguration {

}