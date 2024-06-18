package com.onlydust.marketplace.indexer.domain.models.exposition;

import com.onlydust.marketplace.indexer.domain.models.clean.InstallationEvent;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

@Data
@Builder(access = AccessLevel.PRIVATE, toBuilder = true)
public class GithubAppInstallation {
    Long id;
    GithubAccount account;
    List<GithubRepo> repos;
    Date suspendedAt;
    Set<String> permissions;

    public static GithubAppInstallation of(InstallationEvent event, GithubAccount owner, List<GithubRepo> repos) {
        return GithubAppInstallation.builder()
                .id(event.getInstallationId())
                .account(owner)
                .repos(repos)
                .permissions(getPermissions(event))
                .build();
    }

    public static Set<String> getPermissions(InstallationEvent event) {
        return event.getPermissions().entrySet().stream().flatMap(GithubAppInstallation::toPermissions).collect(toSet());
    }

    private static Stream<? extends String> toPermissions(Map.Entry<String, InstallationEvent.Permission> entry) {
        return switch (entry.getValue()) {
            case read -> Stream.of(toPermission(entry.getKey(), entry.getValue()));
            case write -> Stream.of(toPermission(entry.getKey(), entry.getValue()), toPermission(entry.getKey(), InstallationEvent.Permission.read));
        };
    }

    private static String toPermission(String name, InstallationEvent.Permission permission) {
        return name + ":" + permission.toString();
    }
}
