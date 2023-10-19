package com.onlydust.marketplace.indexer.domain.stubs;

import com.onlydust.marketplace.indexer.domain.models.exposition.GithubRepo;
import com.onlydust.marketplace.indexer.domain.ports.out.GithubRepoRepository;

import java.util.ArrayList;
import java.util.List;

public class GithubRepoRepositoryStub implements GithubRepoRepository {
    private final List<GithubRepo> repos = new ArrayList<>();

    @Override
    public void saveAll(List<GithubRepo> repos) {
        this.repos.addAll(repos);
    }

    public List<GithubRepo> repos() {
        return repos;
    }
}
