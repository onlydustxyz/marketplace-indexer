package com.onlydust.marketplace.indexer.infrastructure.github_archives;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.bigquery.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static com.onlydust.marketplace.indexer.domain.exception.OnlyDustException.internalServerError;

@RequiredArgsConstructor
public class GithubArchivesClient {
    final Properties properties;
    BigQuery bigQuery;

    public TableResult query(String query) {
        return query(query, Map.of());
    }

    public TableResult query(String query, Map<String, QueryParameterValue> params) {
        try {
            final var builder = QueryJobConfiguration.newBuilder(query);
            params.forEach(builder::addNamedParameter);
            return bigQuery().query(builder.build());
        } catch (InterruptedException e) {
            throw internalServerError("Error while querying BigQuery", e);
        }
    }

    private BigQuery bigQuery() {
        return Optional.ofNullable(bigQuery).orElseGet(this::createBigQuery);
    }

    private BigQuery createBigQuery() {
        try {
            return BigQueryOptions.newBuilder()
                    .setProjectId(properties.projectId)
                    .setCredentials(ServiceAccountCredentials.fromStream(new ByteArrayInputStream(properties.credentials.getBytes())))
                    .build()
                    .getService();
        } catch (IOException e) {
            throw internalServerError("Error while creating BigQuery client", e);
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Properties {
        public String projectId;
        public String credentials;
    }
}
