package com.onlydust.marketplace.indexer.bootstrap;

import com.onlydust.marketplace.indexer.bootstrap.it.stubs.PublicEventRawStorageReaderStub;
import com.onlydust.marketplace.indexer.bootstrap.it.stubs.TaskExecutorStub;
import com.onlydust.marketplace.indexer.cli.BatchRunner;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.JobExecutor;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.PublicEventRawStorageReader;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;

import java.util.stream.Stream;

@SpringBootApplication
@EnableConfigurationProperties
public class ApplicationIT {
    @Autowired
    private BatchRunner batchRunner;

    public static void main(String[] args) {
        SpringApplication.run(MarketplaceIndexerApplication.class, args);
    }

    @Bean
    @Primary
    TaskExecutor taskExecutor() {
        return new TaskExecutorStub();
    }

    @Bean
    @Primary
    PublicEventRawStorageReader publicEventRawStorageReader() {
        return new PublicEventRawStorageReaderStub();
    }

    @Bean
    @Primary
    @SneakyThrows
    JobExecutor jobExecutorStub() {
        return new JobExecutor() {
            @Override
            @SneakyThrows
            public void execute(String jobName, String... args) {
                batchRunner.run(Stream.concat(Stream.of(jobName), Stream.of(args))
                        .toArray(String[]::new));
            }
        };
    }
}
