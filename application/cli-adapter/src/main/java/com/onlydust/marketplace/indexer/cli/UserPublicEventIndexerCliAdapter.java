package com.onlydust.marketplace.indexer.cli;

import com.onlydust.marketplace.indexer.domain.jobs.Job;
import com.onlydust.marketplace.indexer.domain.jobs.SequentialJobComposite;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.UserPublicEventsIndexingJobManager;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;

import java.util.Arrays;

@Controller("user_public_event_indexer")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserPublicEventIndexerCliAdapter implements Batch {
    UserPublicEventsIndexingJobManager userPublicEventsIndexingJobManager;

    @Override
    public void run(String... args) {
        final var jobs = Arrays.stream(args)
                .mapToLong(Long::parseLong)
                .mapToObj(userPublicEventsIndexingJobManager::create)
                .toArray(Job[]::new);

        new SequentialJobComposite(jobs).run();
    }
}
