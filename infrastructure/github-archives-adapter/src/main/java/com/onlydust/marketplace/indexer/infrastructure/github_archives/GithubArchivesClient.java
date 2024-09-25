package com.onlydust.marketplace.indexer.infrastructure.github_archives;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.bigquery.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

import static com.onlydust.marketplace.indexer.domain.exception.OnlyDustException.internalServerError;

public class GithubArchivesClient {
    BigQuery bigquery;

    public GithubArchivesClient(Properties properties) {
        try {
            this.bigquery = BigQueryOptions.newBuilder()
                    .setProjectId(properties.projectId)
                    .setCredentials(ServiceAccountCredentials.fromStream(new ByteArrayInputStream(properties.credentials.getBytes())))
                    .build()
                    .getService();
        } catch (IOException e) {
            throw internalServerError("Error while creating BigQuery client", e);
        }
    }

    public TableResult query(String query) {
        return query(query, Map.of());
    }

    public TableResult query(String query, Map<String, QueryParameterValue> params) {
        try {
            final var builder = QueryJobConfiguration.newBuilder(query);
            params.forEach(builder::addNamedParameter);
            return bigquery.query(builder.build());
        } catch (InterruptedException e) {
            throw internalServerError("Error while querying BigQuery", e);
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
