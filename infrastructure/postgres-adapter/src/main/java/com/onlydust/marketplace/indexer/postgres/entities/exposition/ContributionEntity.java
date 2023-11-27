package com.onlydust.marketplace.indexer.postgres.entities.exposition;

import com.onlydust.marketplace.indexer.domain.models.exposition.*;
import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
import lombok.*;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.Date;
import java.util.Optional;

@Entity
@Data
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "contributions", schema = "indexer_exp")
@TypeDef(name = "contribution_type", typeClass = PostgreSQLEnumType.class)
@TypeDef(name = "contribution_status", typeClass = PostgreSQLEnumType.class)
@TypeDef(name = "github_pull_request_review_state", typeClass = PostgreSQLEnumType.class)
public class ContributionEntity {
    @Id
    String id;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    GithubRepoEntity repo;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    GithubAccountEntity contributor;
    @Enumerated(EnumType.STRING)
    @org.hibernate.annotations.Type(type = "contribution_type")
    Type type;
    @Enumerated(EnumType.STRING)
    @org.hibernate.annotations.Type(type = "contribution_status")
    Status status;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    GithubPullRequestEntity pullRequest;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    GithubIssueEntity issue;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    GithubCodeReviewEntity codeReview;
    Date createdAt;
    Date completedAt;
    Long githubNumber;
    String githubStatus;
    String githubTitle;
    String githubHtmlUrl;
    String githubBody;
    Integer githubCommentsCount;
    String repoOwnerLogin;
    String repoName;
    String repoHtmlUrl;
    Long githubAuthorId;
    String githubAuthorLogin;
    String githubAuthorHtmlUrl;
    String githubAuthorAvatarUrl;
    String contributorLogin;
    String contributorHtmlUrl;
    String contributorAvatarUrl;
    @Enumerated(EnumType.STRING)
    @org.hibernate.annotations.Type(type = "github_pull_request_review_state")
    GithubPullRequestEntity.ReviewState prReviewState;

    public static ContributionEntity of(Contribution contribution) {
        final var repo = Optional.ofNullable(contribution.getPullRequest()).map(GithubPullRequest::getRepo)
                .or(() -> Optional.ofNullable(contribution.getIssue()).map(GithubIssue::getRepo))
                .or(() -> Optional.ofNullable(contribution.getCodeReview()).map(GithubCodeReview::getPullRequest).map(GithubPullRequest::getRepo));

        final var author = Optional.ofNullable(contribution.getPullRequest()).map(GithubPullRequest::getAuthor)
                .or(() -> Optional.ofNullable(contribution.getIssue()).map(GithubIssue::getAuthor))
                .or(() -> Optional.ofNullable(contribution.getCodeReview()).map(GithubCodeReview::getAuthor));

        return ContributionEntity.builder()
                .id(contribution.getId())
                .repo(GithubRepoEntity.of(contribution.getRepo()))
                .contributor(GithubAccountEntity.of(contribution.getContributor()))
                .type(Type.of(contribution.getType()))
                .status(Status.of(contribution.getStatus()))
                .pullRequest(contribution.getPullRequest() != null ? GithubPullRequestEntity.of(contribution.getPullRequest()) : null)
                .issue(contribution.getIssue() != null ? GithubIssueEntity.of(contribution.getIssue()) : null)
                .codeReview(contribution.getCodeReview() != null ? GithubCodeReviewEntity.of(contribution.getCodeReview()) : null)
                .createdAt(contribution.getCreatedAt())
                .completedAt(contribution.getCompletedAt())
                .githubNumber(Optional.ofNullable(contribution.getPullRequest()).map(GithubPullRequest::getNumber)
                        .or(() -> Optional.ofNullable(contribution.getIssue()).map(GithubIssue::getNumber))
                        .or(() -> Optional.ofNullable(contribution.getCodeReview()).map(GithubCodeReview::getPullRequest).map(GithubPullRequest::getNumber))
                        .orElse(null))
                .githubStatus(Optional.ofNullable(contribution.getPullRequest()).map(GithubPullRequest::getStatus).map(Enum::toString)
                        .or(() -> Optional.ofNullable(contribution.getIssue()).map(GithubIssue::getStatus).map(Enum::toString))
                        .or(() -> Optional.ofNullable(contribution.getCodeReview()).map(GithubCodeReview::getState).map(Enum::toString))
                        .orElse(null))
                .githubTitle(Optional.ofNullable(contribution.getPullRequest()).map(GithubPullRequest::getTitle)
                        .or(() -> Optional.ofNullable(contribution.getIssue()).map(GithubIssue::getTitle))
                        .or(() -> Optional.ofNullable(contribution.getCodeReview()).map(GithubCodeReview::getPullRequest).map(GithubPullRequest::getTitle))
                        .orElse(null))
                .githubHtmlUrl(Optional.ofNullable(contribution.getPullRequest()).map(GithubPullRequest::getHtmlUrl)
                        .or(() -> Optional.ofNullable(contribution.getIssue()).map(GithubIssue::getHtmlUrl))
                        .or(() -> Optional.ofNullable(contribution.getCodeReview()).map(GithubCodeReview::getPullRequest).map(GithubPullRequest::getHtmlUrl))
                        .orElse(null))
                .githubBody(Optional.ofNullable(contribution.getPullRequest()).map(GithubPullRequest::getBody)
                        .or(() -> Optional.ofNullable(contribution.getIssue()).map(GithubIssue::getBody))
                        .or(() -> Optional.ofNullable(contribution.getCodeReview()).map(GithubCodeReview::getPullRequest).map(GithubPullRequest::getBody))
                        .orElse(null))
                .githubCommentsCount(Optional.ofNullable(contribution.getPullRequest()).map(GithubPullRequest::getCommentsCount)
                        .or(() -> Optional.ofNullable(contribution.getIssue()).map(GithubIssue::getCommentsCount))
                        .or(() -> Optional.ofNullable(contribution.getCodeReview()).map(GithubCodeReview::getPullRequest).map(GithubPullRequest::getCommentsCount))
                        .orElse(null))
                .repoOwnerLogin(repo.map(GithubRepo::getOwner).map(GithubAccount::getLogin).orElse(null))
                .repoName(repo.map(GithubRepo::getName).orElse(null))
                .repoHtmlUrl(repo.map(GithubRepo::getHtmlUrl).orElse(null))
                .githubAuthorId(author.map(GithubAccount::getId).orElse(null))
                .githubAuthorLogin(author.map(GithubAccount::getLogin).orElse(null))
                .githubAuthorHtmlUrl(author.map(GithubAccount::getHtmlUrl).orElse(null))
                .githubAuthorAvatarUrl(author.map(GithubAccount::getAvatarUrl).orElse(null))
                .contributorLogin(contribution.getContributor().getLogin())
                .contributorHtmlUrl(contribution.getContributor().getHtmlUrl())
                .contributorAvatarUrl(contribution.getContributor().getAvatarUrl())
                .prReviewState(Optional.ofNullable(contribution.getPullRequest()).map(GithubPullRequest::getReviewState).map(GithubPullRequestEntity.ReviewState::of).orElse(null))
                .build();
    }

    public enum Type {
        PULL_REQUEST, ISSUE, CODE_REVIEW;

        public static Type of(Contribution.Type type) {
            return switch (type) {
                case PULL_REQUEST -> PULL_REQUEST;
                case ISSUE -> ISSUE;
                case CODE_REVIEW -> CODE_REVIEW;
            };
        }
    }

    public enum Status {
        IN_PROGRESS, COMPLETED, CANCELLED;

        public static Status of(Contribution.Status status) {
            return switch (status) {
                case IN_PROGRESS -> IN_PROGRESS;
                case COMPLETED -> COMPLETED;
                case CANCELLED -> CANCELLED;
            };
        }
    }
}
