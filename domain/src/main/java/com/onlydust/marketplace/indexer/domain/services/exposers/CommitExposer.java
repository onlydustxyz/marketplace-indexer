package com.onlydust.marketplace.indexer.domain.services.exposers;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanAccount;
import com.onlydust.marketplace.indexer.domain.models.clean.CleanCommit;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubCommit;
import com.onlydust.marketplace.indexer.domain.ports.in.Exposer;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.CommitStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.UserFileExtensionStorage;
import com.onlydust.marketplace.indexer.domain.utils.FileUtils;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.*;

@AllArgsConstructor
public class CommitExposer implements Exposer<CleanCommit> {
    UserFileExtensionStorage userFileExtensionStorage;
    CommitStorage commitStorage;

    @Override
    public void expose(CleanCommit commit) {
        commitStorage.save(GithubCommit.of(commit));

        Optional.ofNullable(commit.getAuthor())
                .map(CleanAccount::getId)
                .ifPresent(authorId -> expose(authorId, commit.getModifiedFiles()));
    }

    public void expose(final @NonNull Long authorId, final @NonNull Map<String, Integer> modifiedFiles) {
        final var modifications = modifiedFiles.entrySet().stream()
                .collect(groupingBy(entry -> FileUtils.fileExtension(entry.getKey()).orElse(""),
                        filtering(entry -> !entry.getKey().isEmpty(),
                                mapping(entry -> new Modification(1, entry.getValue()),
                                        reducing(null, Modification::add)))));

        modifications.forEach((fileExtension, modification) ->
                userFileExtensionStorage.addModificationsForUserAndExtension(authorId,
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
