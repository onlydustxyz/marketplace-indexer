package com.onlydust.marketplace.indexer.api.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlydust.marketplace.indexer.domain.exception.OnlyDustException;
import io.netty.handler.codec.http.HttpMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.lang.String.format;
import static java.net.http.HttpRequest.BodyPublishers.noBody;
import static java.net.http.HttpRequest.BodyPublishers.ofByteArray;
import static java.util.Objects.isNull;

@AllArgsConstructor
public class ApiHttpClient {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Config config;

    private HttpRequest.Builder builderFromAuthorizations() {
        return HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .header("Api-Key", config.getApiKey());
    }

    public <RequestBody, ResponseBody> ResponseBody sendRequest(final String path,
                                                                final HttpMethod method,
                                                                final RequestBody requestBody,
                                                                final Class<ResponseBody> responseClass) {
        try {
            final HttpResponse<byte[]> httpResponse = httpClient.send(
                    builderFromAuthorizations()
                            .uri(URI.create(config.getBaseUri() + path))
                            .method(method.name(),
                                    isNull(requestBody) ? noBody() :
                                            ofByteArray(objectMapper.writeValueAsBytes(requestBody)))
                            .build(),
                    HttpResponse.BodyHandlers.ofByteArray()
            );
            final int statusCode = httpResponse.statusCode();
            if (statusCode == HttpStatus.UNAUTHORIZED.value()) {
                throw OnlyDustException.unAuthorized(format("Unauthorized error when calling %s on API", path));
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                throw OnlyDustException.forbidden(format("Forbidden error when calling %s on API", path));
            } else if (statusCode != HttpStatus.OK.value() &&
                       statusCode != HttpStatus.CREATED.value() &&
                       statusCode != HttpStatus.ACCEPTED.value() &&
                       statusCode != HttpStatus.NO_CONTENT.value()) {
                throw OnlyDustException.internalServerError(format("Unknown error (status %d) when calling %s on API", statusCode, path));
            } else if (Void.class.isAssignableFrom(responseClass)) {
                return null;
            }
            return objectMapper.readValue(httpResponse.body(), responseClass);

        } catch (JsonProcessingException e) {
            throw OnlyDustException.internalServerError("Fail to serialize request", e);
        } catch (IOException | InterruptedException e) {
            throw OnlyDustException.internalServerError("Fail send request", e);
        }
    }

    @Data
    public static class Config {
        String baseUri;
        String apiKey;
    }
}
