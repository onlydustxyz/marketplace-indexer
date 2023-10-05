package com.onlydust.marketplace.indexer.domain.stubs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlydust.marketplace.indexer.domain.model.raw.RawPullRequest;
import com.onlydust.marketplace.indexer.domain.model.raw.RawSocialAccount;
import com.onlydust.marketplace.indexer.domain.model.raw.RawUser;
import com.onlydust.marketplace.indexer.domain.ports.out.RawStorageRepository;

import java.io.IOException;
import java.util.*;

public class RawStorageRepositoryStub implements RawStorageRepository {
    final List<RawUser> users = new ArrayList<>();
    final Map<Integer, List<RawSocialAccount>> userSocialAccounts = new HashMap<>();
    final List<RawPullRequest> pullRequests = new ArrayList<>();

    public static <T> T load(String path, Class<T> type) {
        final var inputStream = type.getResourceAsStream(path);
        try {
            return new ObjectMapper().readValue(inputStream, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<RawUser> user(Integer userId) {
        return users.stream().filter(user -> user.getId().equals(userId)).findFirst();
    }

    @Override
    public Optional<List<RawSocialAccount>> userSocialAccounts(Integer userId) {
        try {
            return Optional.of(userSocialAccounts.get(userId));
        } catch (NullPointerException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<RawPullRequest> pullRequest(String repoOwner, String repoName, Integer prNumber) {
        return pullRequests.stream().filter(
                        pr -> pr.getBase().getRepo().getOwner().getLogin().equals(repoOwner) &&
                                pr.getBase().getRepo().getName().equals(repoName) &&
                                pr.getNumber().equals(prNumber))
                .findFirst();
    }

    public void feedWith(RawUser... rawUsers) {
        Arrays.stream(rawUsers).sequential().forEach(this::save);
    }

    public void feedWith(Integer userId, RawSocialAccount... rawSocialAccounts) {
        this.userSocialAccounts.put(userId, Arrays.stream(rawSocialAccounts).toList());
    }

    public void feedWith(RawPullRequest... pullRequests) {
        Arrays.stream(pullRequests).sequential().forEach(this::save);
    }

    @Override
    public void save(RawUser user) {
        users.add(user);
    }

    @Override
    public void save(Integer userId, List<RawSocialAccount> socialAccounts) {
        userSocialAccounts.put(userId, socialAccounts);
    }

    @Override
    public void save(RawPullRequest pullRequest) {
        pullRequests.add(pullRequest);
    }

    public List<RawUser> users() {
        return users;
    }

    public Map<Integer, List<RawSocialAccount>> userSocialAccounts() {
        return userSocialAccounts;
    }

    public List<RawPullRequest> pullRequests() {
        return pullRequests;
    }
}
