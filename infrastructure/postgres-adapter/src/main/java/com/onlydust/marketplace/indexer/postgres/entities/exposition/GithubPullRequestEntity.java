package com.onlydust.marketplace.indexer.postgres.entities.exposition;

import com.onlydust.marketplace.indexer.domain.models.exposition.GithubPullRequest;
import io.hypersistence.utils.hibernate.type.array.StringArrayType;
import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Data
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "github_pull_requests", schema = "indexer_exp")
@TypeDef(name = "github_pull_request_status", typeClass = PostgreSQLEnumType.class)
@TypeDef(name = "github_pull_request_review_state", typeClass = PostgreSQLEnumType.class)
@TypeDef(name = "text[]", typeClass = StringArrayType.class)
public class GithubPullRequestEntity {
    @Id
    Long id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    GithubRepoEntity repo;
    Long number;
    String title;
    @Enumerated(EnumType.STRING)
    @Type(type = "github_pull_request_status")
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
    @Type(type = "github_pull_request_review_state")
    ReviewState reviewState;
    Integer commitCount;
    @Type(type = "text[]")
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

    public static GithubPullRequestEntity of(GithubPullRequest pullRequest) {
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
                .commitCount(pullRequest.getCommitCounts().values().stream().mapToInt(Long::intValue).sum())
                .closingIssues(pullRequest.getClosingIssues().stream().map(GithubIssueEntity::of).collect(Collectors.toUnmodifiableSet()))
                .commitCounts(pullRequest.getCommitCounts().entrySet().stream()
                        .map(e -> GithubPullRequestCommitCountEntity.of(pullRequest.getId(), GithubAccountEntity.of(e.getKey()), e.getValue()))
                        .collect(Collectors.toUnmodifiableSet()))
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
