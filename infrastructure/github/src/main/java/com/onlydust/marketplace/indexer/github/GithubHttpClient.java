package com.onlydust.marketplace.indexer.github;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlydust.marketplace.indexer.domain.exception.NotFound;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


@Slf4j
@AllArgsConstructor
public class GithubHttpClient {
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final Config config;

    private <T> T decode(byte[] data, Class<T> classType) {
        try {
            return objectMapper.readValue(data, classType);
        } catch (IOException e) {
            throw new NotFound("Unable to deserialize github response", e);
        }
    }

    private HttpResponse<byte[]> fetch(final String uri) {
        try {
            final var requestBuilder = HttpRequest.newBuilder(URI.create(config.baseUri + uri)).headers("Authorization", "Bearer " + config.personalAccessToken).GET();
            return httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofByteArray());
        } catch (IOException | InterruptedException e) {
            throw new NotFound("Unable to fetch github API:" + uri, e);
        }
    }

    public <ResponseBody> Optional<ResponseBody> get(String uri, Class<ResponseBody> responseClass) {
        final var httpResponse = fetch(uri);
        return switch (httpResponse.statusCode()) {
            case 200 -> Optional.of(decode(httpResponse.body(), responseClass));
            case 404 -> Optional.empty();
            default -> throw new NotFound("Unable to fetch github API:" + uri);
        };
    }

    public <ResponseBody> Stream<ResponseBody> stream(String uri, Class<ResponseBody[]> responseClass) {
        final var page = new Page<ResponseBody>(uri, responseClass);
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(page, Spliterator.ORDERED), false);
    }

    @ToString
    @Data
    public static class Config {
        private String baseUri;
        private String personalAccessToken;
    }

    private class Page<T> implements Iterator<T> {
        GithubPageLinks links;
        Deque<T> content;
        Class<T[]> classType;

        public Page(String uri, Class<T[]> classType) {
            this.classType = classType;
            fetchNextPage(uri);
        }

        private void fetchNextPage(String uri) {
            final var httpResponse = fetch(uri);
            switch (httpResponse.statusCode()) {
                case 200:
                    final var links = httpResponse.headers().firstValue("Links").map(GithubPageLinks::of).orElse(new GithubPageLinks());
                    content.addAll(Arrays.asList(decode(httpResponse.body(), classType)));
                    break;
                case 404:
                    break;
                default:
                    throw new NotFound("Unable to fetch github API:" + uri);
            }
        }

        @Override
        public boolean hasNext() {
            return !content.isEmpty() || !Objects.isNull(links.getNext());
        }

        @Override
        public T next() {
            if (content.isEmpty()) {
                fetchNextPage(links.getNext());
            }

            return content.poll();
        }
    }
}
