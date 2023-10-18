package com.onlydust.marketplace.indexer.domain.services;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanAccount;
import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;
import com.onlydust.marketplace.indexer.domain.models.clean.InstallationEvent;
import com.onlydust.marketplace.indexer.domain.models.raw.RawInstallationEvent;
import com.onlydust.marketplace.indexer.domain.ports.out.EventListener;
import com.onlydust.marketplace.indexer.domain.ports.out.RawInstallationEventRepository;
import com.onlydust.marketplace.indexer.domain.ports.out.RawStorageReader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@AllArgsConstructor
@Slf4j
public class EventProcessorService {
    private final RawInstallationEventRepository rawInstallationEventRepository;
    private final EventListener<InstallationEvent> installationEventEventListener;
    private final RawStorageReader rawStorageReader;

    public void process(RawInstallationEvent rawEvent) {
        rawInstallationEventRepository.save(rawEvent);
        final var account = CleanAccount.of(rawEvent.getInstallation().getAccount());
        final var event = InstallationEvent.builder()
                .action(InstallationEvent.Action.valueOf(rawEvent.getAction().toUpperCase()))
                .installationId(rawEvent.getInstallation().getId())
                .account(account)
                .repos(rawEvent.getRepositories().stream()
                        .map(eventRepo -> rawStorageReader.repo(eventRepo.getId()).map(repo -> CleanRepo.of(repo, account)).orElse(null))
                        .filter(Objects::nonNull).toList())
                .build();
        installationEventEventListener.onEvent(event);
    }
}
