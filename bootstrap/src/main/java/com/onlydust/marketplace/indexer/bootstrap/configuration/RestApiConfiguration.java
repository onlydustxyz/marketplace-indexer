package com.onlydust.marketplace.indexer.bootstrap.configuration;

import com.onlydust.marketplace.indexer.domain.services.IndexingService;
import com.onlydust.marketplace.indexer.rest.api.UsersRestApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestApiConfiguration {
    @Bean
    public UsersRestApi usersRestApi(final IndexingService indexingService) {
        return new UsersRestApi(indexingService);
    }
}
