package com.onlydust.marketplace.indexer.domain.stubs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlydust.marketplace.indexer.domain.model.raw.RawSocialAccount;
import com.onlydust.marketplace.indexer.domain.model.raw.RawUser;
import com.onlydust.marketplace.indexer.domain.ports.out.RawStorageRepository;

import java.io.IOException;
import java.util.*;

public class RawStorageRepositoryStub implements RawStorageRepository {
    final List<RawUser> rawUsers = new ArrayList<>();
    final Map<Integer, List<RawSocialAccount>> userSocialAccounts = new HashMap<>();

    public static <T> T load(String path, Class<T> type) {
        final var inputStream = type.getResourceAsStream(path);
        try {
            return new ObjectMapper().readValue(inputStream, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<RawUser> userById(Integer userId) {
        return rawUsers.stream().filter(user -> user.getId().equals(userId)).findFirst();
    }

    @Override
    public Optional<List<RawSocialAccount>> userSocialAccountsById(Integer userId) {
        try {
            return Optional.of(userSocialAccounts.get(userId));
        } catch (NullPointerException e) {
            return Optional.empty();
        }
    }

    public void feedWith(RawUser... rawUsers) {
        Arrays.stream(rawUsers).sequential().forEach(this::save);
    }

    public void feedWith(Integer userId, RawSocialAccount... rawSocialAccounts) {
        this.userSocialAccounts.put(userId, Arrays.stream(rawSocialAccounts).toList());
    }

    @Override
    public void save(RawUser rawUser) {
        rawUsers.add(rawUser);
    }

    @Override
    public void save(Integer userId, List<RawSocialAccount> rawSocialAccounts) {
        userSocialAccounts.put(userId, rawSocialAccounts);
    }

    public List<RawUser> users() {
        return rawUsers;
    }

    public List<RawSocialAccount> userSocialAccounts(Integer userId) {
        return userSocialAccounts.get(userId);
    }
}
