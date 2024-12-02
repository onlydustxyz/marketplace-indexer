package com.onlydust.marketplace.indexer.cli;

import com.onlydust.marketplace.indexer.domain.jobs.Job;
import com.onlydust.marketplace.indexer.domain.jobs.SequentialJobComposite;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.UserPublicEventsIndexingJobManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;

import java.util.Arrays;

@Slf4j
@AllArgsConstructor
public class UserPublicEventIndexerCliAdapter implements CommandLineRunner {
    private final UserPublicEventsIndexingJobManager userPublicEventsIndexingJobManager;

    @Override
    public void run(String... args) {
        if (args.length == 0 || !args[0].equals("user_public_event_indexer")) return;

        final var jobs = Arrays.stream(args)
                .skip(1)
                .mapToLong(Long::parseLong)
                .mapToObj(userPublicEventsIndexingJobManager::create)
                .toArray(Job[]::new);

        LOGGER.info("Indexing {} users public events", jobs.length);
        new SequentialJobComposite(jobs).run();
    }
}
