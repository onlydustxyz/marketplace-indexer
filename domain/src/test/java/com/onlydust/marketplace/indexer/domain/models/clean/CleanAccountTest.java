package com.onlydust.marketplace.indexer.domain.models.clean;

import com.onlydust.marketplace.indexer.domain.models.raw.RawAccount;
import com.onlydust.marketplace.indexer.domain.models.raw.RawSocialAccount;
import com.onlydust.marketplace.indexer.domain.stubs.RawStorageWriterStub;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CleanAccountTest {

    @Test
    void should_return_social_accounts() {
        // given
        final var anthony = RawStorageWriterStub.load("/github/users/anthony.json", RawAccount.class);
        final var account = CleanAccount.of(anthony, List.of(
                new RawSocialAccount("twitter", "https://twitter.com/onlydust"),
                new RawSocialAccount("linkedin", "https://www.linkedin.com/in/onlydust/"),
                new RawSocialAccount("generic", "https://t.me/onlydust")
        ));

        // when
        final var twitter = account.getTwitter();
        final var linkedin = account.getLinkedin();
        final var telegram = account.getTelegram();

        // then
        assertThat(twitter).isEqualTo("https://twitter.com/onlydust");
        assertThat(linkedin).isEqualTo("https://www.linkedin.com/in/onlydust/");
        assertThat(telegram).isEqualTo("https://t.me/onlydust");
    }

    @Test
    void should_return_telegram() {
        // given
        final var anthony = RawStorageWriterStub.load("/github/users/anthony.json", RawAccount.class);
        final var account = CleanAccount.of(anthony, List.of(
                new RawSocialAccount("generic", "https://telegram.me/onlydust")
        ));

        // when
        final var telegram = account.getTelegram();

        // then
        assertThat(telegram).isEqualTo("https://telegram.me/onlydust");
    }

    @Test
    void should_return_null_social_accounts() {
        // given
        final var anthony = RawStorageWriterStub.load("/github/users/anthony.json", RawAccount.class);
        final var account = CleanAccount.of(anthony, List.of());

        // when
        final var twitter = account.getTwitter();
        final var linkedin = account.getLinkedin();
        final var telegram = account.getTelegram();

        // then
        assertThat(twitter).isNull();
        assertThat(linkedin).isNull();
        assertThat(telegram).isNull();
    }
}