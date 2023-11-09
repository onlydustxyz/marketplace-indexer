package com.onlydust.marketplace.indexer.github.adapters;

import com.onlydust.marketplace.indexer.domain.exception.OnlyDustException;
import com.onlydust.marketplace.indexer.github.GithubTokenProvider;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;


@Slf4j
@AllArgsConstructor
public class GithubAppJwtProvider implements GithubTokenProvider {
    private final Config config;

    @Override
    public Optional<String> accessToken() {
        return Optional.ofNullable(generateSignedJwtToken());
    }

    private String generateSignedJwtToken() {
        try {
            final RSAPrivateKey rsaPrivateKey = getPrivateKey();
            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.RS256;

            final long now = Instant.now().getEpochSecond();
            JwtBuilder builder = Jwts.builder().claim("iss", config.getAppId())
                    .claim("exp", now + 10 * 60)
                    .claim("iat", now - 60)
                    .signWith(rsaPrivateKey, signatureAlgorithm);
            return builder.compact();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw OnlyDustException.internalServerError("Error while generating JWT token for Github App", e);
        }
    }

    private RSAPrivateKey getPrivateKey() throws NoSuchAlgorithmException,
            InvalidKeySpecException {
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(config.getPrivateKey()));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return (RSAPrivateKey) keyFactory.generatePrivate(spec);
    }

    @ToString
    @Data
    public static class Config {
        private String appId;
        private String privateKey;
    }
}
