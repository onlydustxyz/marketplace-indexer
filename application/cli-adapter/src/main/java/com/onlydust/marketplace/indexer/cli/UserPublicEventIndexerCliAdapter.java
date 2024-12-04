package com.onlydust.marketplace.indexer.cli;

import com.onlydust.marketplace.indexer.domain.ports.in.jobs.UserPublicEventsIndexingJobManager;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;

@Controller("user_public_event_indexer")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserPublicEventIndexerCliAdapter implements Batch {
    UserPublicEventsIndexingJobManager userPublicEventsIndexingJobManager;

    @Override
    public void run(String... args) {
        userPublicEventsIndexingJobManager.create(Long.parseLong(args[0])).run();
    }
}
