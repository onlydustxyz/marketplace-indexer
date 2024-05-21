package com.onlydust.marketplace.indexer.rest.api;

import com.onlydust.marketplace.indexer.domain.exception.OnlyDustException;
import com.onlydust.marketplace.indexer.github.adapters.GithubAppContextAdapter;
import com.onlydust.marketplace.indexer.rest.api.model.GetGithubAccessToken200Response;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.mutable.MutableObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tags(@Tag(name = "Debug"))
@AllArgsConstructor
public class DebugRestApi implements DebugApi {
    private final GithubAppContextAdapter githubAppContextAdapter;

    @Override
    public ResponseEntity<GetGithubAccessToken200Response> getGithubAccessToken(Long installationId) {

        final MutableObject<String> accessToken = new MutableObject<>();
        githubAppContextAdapter.withGithubApp(installationId, () -> {
            accessToken.setValue(githubAppContextAdapter.accessToken()
                    .orElseThrow(() -> OnlyDustException.notFound("No access token found for installation " + installationId)));
        });

        return ResponseEntity.ok(new GetGithubAccessToken200Response().token(accessToken.getValue()));
    }
}
