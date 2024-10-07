package com.onlydust.marketplace.indexer.domain.services.exposers;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanCommit;
import com.onlydust.marketplace.indexer.domain.ports.in.Exposer;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.UserFileExtensionStorage;
import com.onlydust.marketplace.indexer.domain.utils.FileUtils;
import lombok.AllArgsConstructor;

import java.util.Optional;

import static java.util.stream.Collectors.toSet;

@AllArgsConstructor
public class CommitExposer implements Exposer<CleanCommit> {
    UserFileExtensionStorage userFileExtensionStorage;

    @Override
    public void expose(CleanCommit commit) {
        final var fileExtensions = commit.getModifiedFiles().keySet().stream()
                .map(FileUtils::fileExtension)
                .flatMap(Optional::stream)
                .collect(toSet());
        userFileExtensionStorage.addCommit(commit.getAuthor().getId(), fileExtensions);
    }
}
