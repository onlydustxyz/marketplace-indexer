package com.onlydust.marketplace.indexer.domain.services.exposers;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanCommit;
import com.onlydust.marketplace.indexer.domain.ports.in.Exposer;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.UserFileExtensionStorage;
import com.onlydust.marketplace.indexer.domain.utils.FileUtils;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import static java.util.stream.Collectors.*;

@AllArgsConstructor
public class CommitExposer implements Exposer<CleanCommit> {
    UserFileExtensionStorage userFileExtensionStorage;

    @Override
    public void expose(CleanCommit commit) {
        final var modifications = commit.getModifiedFiles().entrySet().stream()
                .collect(groupingBy(entry -> FileUtils.fileExtension(entry.getKey()).orElse(""),
                        filtering(entry -> !entry.getKey().isEmpty(),
                                mapping(entry -> new Modification(1, entry.getValue()),
                                        reducing(null, Modification::add)))));

        modifications.forEach((fileExtension, modification) ->
                userFileExtensionStorage.addModificationsForUserAndExtension(commit.getAuthor().getId(),
                        fileExtension,
                        1,
                        modification.fileCount(),
                        modification.modificationCount()));
    }

    private record Modification(int fileCount, int modificationCount) {
        public static Modification add(final Modification left, final @NonNull Modification right) {
            return left == null ? right : new Modification(left.fileCount + right.fileCount,
                    left.modificationCount + right.modificationCount);
        }
    }
}
