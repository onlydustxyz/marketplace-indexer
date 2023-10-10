package com.onlydust.marketplace.indexer.domain.services;

import com.onlydust.marketplace.indexer.domain.mappers.RepoMapper;
import com.onlydust.marketplace.indexer.domain.mappers.UserMapper;
import com.onlydust.marketplace.indexer.domain.models.clean.InstallationEvent;
import com.onlydust.marketplace.indexer.domain.models.raw.RawInstallationEvent;
import com.onlydust.marketplace.indexer.domain.ports.out.EventListener;
import com.onlydust.marketplace.indexer.domain.ports.out.RawInstallationEventRepository;
import com.onlydust.marketplace.indexer.domain.ports.out.RawStorageReader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class EventProcessorService {
    private final RawInstallationEventRepository rawInstallationEventRepository;
    private final EventListener<InstallationEvent> installationEventEventListener;
    private final RawStorageReader rawStorageReader;

    public void process(RawInstallationEvent rawEvent) {
        rawInstallationEventRepository.save(rawEvent);
        final var event = InstallationEvent.builder()
                .installationId(rawEvent.getInstallation().getId())
                .account(UserMapper.map(rawEvent.getInstallation().getAccount()))
                .repos(rawEvent.getRepositories().stream().map(eventRepo -> {
                    final var repo = rawStorageReader.repo(eventRepo.getId()).orElseThrow();
                    return RepoMapper.map(repo);
                }).toList())
                .build();
        installationEventEventListener.onEvent(event);
    }
}
