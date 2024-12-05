package com.onlydust.marketplace.indexer.cli;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;

import java.util.ArrayDeque;

import static java.util.Arrays.asList;

@Controller
@Profile("cli")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@EnableAutoConfiguration
public class BatchRunner implements CommandLineRunner, ApplicationContextAware {
    ApplicationContext applicationContext;

    @Override
    public void run(String... args) throws Exception {
        final var deque = new ArrayDeque<>(asList(args));
        if (deque.isEmpty()) return;

        final var batch = applicationContext.getBean(deque.pollFirst(), Batch.class);
        LOGGER.info("Running batch {}", batch.getClass().getSimpleName());
        batch.run(deque.toArray(String[]::new));
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
