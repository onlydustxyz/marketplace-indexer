package com.onlydust.marketplace.indexer.bootstrap.it;

import com.onlydust.marketplace.indexer.postgres.repositories.raw.EventsInboxEntityRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GithubStarEventsIT extends IntegrationTest {
    @Autowired
    EventsInboxEntityRepository eventsInboxEntityRepository;

    @Test
    @Order(1)
    void init() {
        processEventsFromPaths("installation",
                "/github/webhook/events/installation/installation_created_old.json"
        );
    }

    @Test
    @Order(2)
    void should_handle_star_events() {
        // When
        processEventsFromPaths("star",
                "/github/webhook/events/star/cairo-streams-starred.json"
        );
        assertAllEventsAreProcessed("star");
    }
}
