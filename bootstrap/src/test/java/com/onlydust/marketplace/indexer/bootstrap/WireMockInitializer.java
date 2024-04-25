package com.onlydust.marketplace.indexer.bootstrap;

import lombok.NonNull;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class WireMockInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(final @NonNull ConfigurableApplicationContext context) {
        WiremockServerRegistration.builder()
                .beanName("githubWireMockServer")
                .stubLocation("github")
                .property("infrastructure.github.baseUri")
                .build()
                .register(context);

        WiremockServerRegistration.builder()
                .beanName("githubForAppWireMockServer")
                .stubLocation("github")
                .property("infrastructure.github-for-app.base-uri")
                .build()
                .register(context);
    }
}
