package com.onlydust.marketplace.indexer.infrastructure.aws_athena;

import com.onlydust.marketplace.indexer.infrastructure.aws_athena.adapters.AwsAthenaPublicEventRawStorageReaderAdapter;
import software.amazon.awssdk.regions.Region;

import java.time.ZonedDateTime;
import java.util.concurrent.Executors;

class AwsAthenaClientTest {
    private final AwsAthenaClient client = new AwsAthenaClient(Executors.newSingleThreadScheduledExecutor(),
            new AwsAthenaClient.Properties(Region.EU_WEST_3, "gha_db", "s3://gha-athena-results", 1));

    private final AwsAthenaPublicEventRawStorageReaderAdapter adapter = new AwsAthenaPublicEventRawStorageReaderAdapter(client);

    //    @Test
    void query() {
        adapter.userPublicEvents(595505L, ZonedDateTime.parse("2023-01-01T00:00:00Z"))
                .limit(3) // First row is the header
                .forEach(e -> System.out.println(e.payload()));
    }
}