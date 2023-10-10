package com.onlydust.marketplace.indexer.rest.github.security;

import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.security.authentication.BadCredentialsException;

public class GithubSignatureVerifier {
    public static void validateWebhook(final byte[] githubWebhookBodyBytes, final String webhookSecret,
                                       final String github256Signature) {
        final String currentSha256Signature = "sha256=" + hmac(githubWebhookBodyBytes, webhookSecret);
        if (!currentSha256Signature.equals(github256Signature)) {
            throw new BadCredentialsException("Invalid sha256 signature");
        }

    }

    public static String hmac(final byte[] data, final String key) {
        return new HmacUtils("HmacSHA256", key).hmacHex(data);
    }
}
