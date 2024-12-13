package com.onlydust.marketplace.indexer.infrastructure.aws_athena;

import java.time.ZonedDateTime;
import java.util.concurrent.Executors;

import com.onlydust.marketplace.indexer.infrastructure.aws_athena.adapters.AwsAthenaPublicEventRawStorageReaderAdapter;

import software.amazon.awssdk.regions.Region;

class AwsAthenaClientTest {
    private final AwsAthenaClient.Properties properties = new AwsAthenaClient.Properties(Region.EU_WEST_3, "gha_db", "116981777789", "s3://gha-athena-results", 1, 1000, 4, 900);
    private final AwsAthenaClient client = new AwsAthenaClient(Executors.newSingleThreadScheduledExecutor(), properties);

    private final AwsAthenaPublicEventRawStorageReaderAdapter adapter = new AwsAthenaPublicEventRawStorageReaderAdapter(client, properties);

    //    @Test
    void query() {
        adapter.userPublicEvents(595505L, ZonedDateTime.parse("2023-01-01T00:00:00Z"))
                .limit(3) // First row is the header
                .forEach(e -> System.out.println(e.payload()));
    }
}