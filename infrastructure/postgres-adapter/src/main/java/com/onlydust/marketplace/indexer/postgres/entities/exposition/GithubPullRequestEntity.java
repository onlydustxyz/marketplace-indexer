package com.onlydust.marketplace.indexer.postgres.entities.exposition;

import com.onlydust.marketplace.indexer.domain.models.exposition.GithubCommit;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubPullRequest;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.hibernate.type.SqlTypes;

import java.util.Date;
import java.util.Set;

import static java.util.stream.Collectors.*;

@Entity
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Getter
@Table(name = "github_pull_requests", schema = "indexer_exp")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GithubPullRequestEntity {
    @Id
    Long id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    GithubRepoEntity repo;

    Long number;
    String title;
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "github_pull_request_status")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    Status status;
    Date createdAt;
    Date closedAt;
    Date mergedAt;
    String body;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    GithubAccountEntity author;

    String htmlUrl;
    Integer commentsCount;
    Boolean draft;
    String repoOwnerLogin;
    String repoName;
    String repoHtmlUrl;
    String authorLogin;
    String authorHtmlUrl;
    String authorAvatarUrl;
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "github_pull_request_review_state")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    ReviewState reviewState;
    Integer commitCount;
    @JdbcTypeCode(SqlTypes.ARRAY)
    String[] mainFileExtensions;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "github_pull_requests_closing_issues",
            schema = "indexer_exp",
            joinColumns = @JoinColumn(name = "pull_request_id"),
            inverseJoinColumns = @JoinColumn(name = "issue_id"))
    Set<GithubIssueEntity> closingIssues;

    @OneToMany(mappedBy = "pullRequestId", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    Set<GithubPullRequestCommitCountEntity> commitCounts;

    @OneToMany(mappedBy = "pullRequestId", cascade = CascadeType.ALL)
    Set<GithubCommitEntity> commits;

    public static GithubPullRequestEntity of(GithubPullRequest pullRequest) {
        final var commitCounts = pullRequest.getCommits().stream()
                .filter(c -> c.getAuthorId().isPresent())
                .collect(groupingBy(GithubCommit::getAuthor, counting()));

        return GithubPullRequestEntity.builder()
                .id(pullRequest.getId())
                .repo(GithubRepoEntity.of(pullRequest.getRepo()))
                .number(pullRequest.getNumber())
                .title(pullRequest.getTitle())
                .status(Status.of(pullRequest.getStatus()))
                .createdAt(pullRequest.getCreatedAt())
                .closedAt(pullRequest.getClosedAt())
                .mergedAt(pullRequest.getMergedAt())
                .body(pullRequest.getBody())
                .author(GithubAccountEntity.of(pullRequest.getAuthor()))
                .htmlUrl(pullRequest.getHtmlUrl())
                .commentsCount(pullRequest.getCommentsCount())
                .draft(pullRequest.getDraft())
                .repoOwnerLogin(pullRequest.getRepo().getOwner().getLogin())
                .repoName(pullRequest.getRepo().getName())
                .repoHtmlUrl(pullRequest.getRepo().getHtmlUrl())
                .authorLogin(pullRequest.getAuthor().getLogin())
                .authorHtmlUrl(pullRequest.getAuthor().getHtmlUrl())
                .authorAvatarUrl(pullRequest.getAuthor().getAvatarUrl())
                .reviewState(ReviewState.of(pullRequest.getReviewState()))
                .commitCount(pullRequest.getCommits().size())
                .closingIssues(pullRequest.getClosingIssues().stream().map(GithubIssueEntity::of).collect(toUnmodifiableSet()))
                .commitCounts(commitCounts.entrySet().stream()
                        .map(e -> GithubPullRequestCommitCountEntity.of(pullRequest.getId(), GithubAccountEntity.of(e.getKey()), e.getValue()))
                        .collect(toUnmodifiableSet()))
                .commits(pullRequest.getCommits().stream()
                        .filter(c -> c.getAuthorId().isPresent())
                        .map(c -> GithubCommitEntity.of(c.getSha(), pullRequest.getId(), GithubAccountEntity.of(c.getAuthor())))
                        .collect(toUnmodifiableSet()))
                .mainFileExtensions(pullRequest.getMainFileExtensions().toArray(String[]::new))
                .build();
    }

    public enum Status {
        OPEN, CLOSED, MERGED, DRAFT;

        public static Status of(GithubPullRequest.Status status) {
            return switch (status) {
                case OPEN -> OPEN;
                case DRAFT -> DRAFT;
                case CLOSED -> CLOSED;
                case MERGED -> MERGED;
            };
        }
    }

    public enum ReviewState {
        PENDING_REVIEWER, UNDER_REVIEW, APPROVED, CHANGES_REQUESTED;

        public static ReviewState of(GithubPullRequest.ReviewState reviewState) {
            return switch (reviewState) {
                case PENDING_REVIEWER -> PENDING_REVIEWER;
                case UNDER_REVIEW -> UNDER_REVIEW;
                case APPROVED -> APPROVED;
                case CHANGES_REQUESTED -> CHANGES_REQUESTED;
            };
        }
    }

}
