package com.onlydust.marketplace.indexer.domain.models.raw;


import io.reflectoring.diffparser.api.UnifiedDiffParser;
import io.reflectoring.diffparser.api.model.Diff;
import io.reflectoring.diffparser.api.model.Hunk;
import io.reflectoring.diffparser.api.model.Line;
import lombok.*;

import java.util.List;
import java.util.Objects;

@Value
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
@ToString(callSuper = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RawPullRequestDiff extends JsonDocument {
    List<FileStat> modifiedFiles;

    public static RawPullRequestDiff of(byte[] diff) {
        final var parser = new UnifiedDiffParser();
        final var modifiedFiles = parser.parse(sanitize(diff)).stream()
                .map(FileStat::of)
                .toList();

        return new RawPullRequestDiff(modifiedFiles);
    }

    private static byte[] sanitize(byte[] diff) {
        return new String(diff).replaceAll("(@@.*@@)", "$1\n").getBytes();
    }

    public record FileStat(String path, Long linesAdded, Long linesDeleted) {
        private static FileStat of(Diff diff) {
            return new FileStat(filename(diff), addedLines(diff), deletedLines(diff));
        }

        private static String filename(Diff diff) {
            return (Objects.equals(diff.getFromFileName(), "/dev/null") ? diff.getToFileName() : diff.getFromFileName()).substring(2);
        }

        private static Long addedLines(Diff diff) {
            return diff.getHunks().stream().map(FileStat::addedLines).reduce(0L, Long::sum);
        }

        private static Long deletedLines(Diff diff) {
            return diff.getHunks().stream().map(FileStat::deletedLines).reduce(0L, Long::sum);
        }

        private static Long addedLines(Hunk hunk) {
            return hunk.getLines().stream().filter(l -> l.getLineType() == Line.LineType.TO).count();
        }

        private static Long deletedLines(Hunk hunk) {
            return hunk.getLines().stream().filter(l -> l.getLineType() == Line.LineType.FROM).count();
        }
    }
}
