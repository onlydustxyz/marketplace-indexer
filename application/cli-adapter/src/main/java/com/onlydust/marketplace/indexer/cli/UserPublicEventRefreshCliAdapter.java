package com.onlydust.marketplace.indexer.cli;

import com.onlydust.marketplace.indexer.domain.ports.in.indexers.UserPublicEventsIndexer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;

import java.time.ZonedDateTime;

@Controller("user_public_event_refresh")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserPublicEventRefreshCliAdapter implements Batch {
    UserPublicEventsIndexer userPublicEventsIndexer;

    @Override
    public void run(String... args) {
        userPublicEventsIndexer.indexAllUsers(ZonedDateTime.parse(args[0]));
    }
}
