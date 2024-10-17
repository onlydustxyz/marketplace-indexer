package com.onlydust.marketplace.indexer.postgres.entities.exposition;

import com.onlydust.marketplace.indexer.domain.models.exposition.*;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.hibernate.type.SqlTypes;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;

@Entity
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor(force = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Table(name = "grouped_contributions", schema = "indexer_exp")
public class GroupedContributionEntity {
    @Id
    @org.hibernate.annotations.Generated
    UUID id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    GithubRepoEntity repo;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "grouped_contribution_contributors", schema = "indexer_exp",
            joinColumns = @JoinColumn(name = "grouped_contribution_id"),
            inverseJoinColumns = @JoinColumn(name = "contributor_id"))
    Set<GithubAccountEntity> contributors;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "contribution_type")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    ContributionEntity.Type type;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "contribution_status")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    ContributionEntity.Status status;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    GithubPullRequestEntity pullRequest;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    GithubIssueEntity issue;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    GithubCodeReviewEntity codeReview;

    Date createdAt;
    Date updatedAt;
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

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "github_pull_request_review_state")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    GithubPullRequestEntity.ReviewState prReviewState;

    @JdbcTypeCode(SqlTypes.ARRAY)
    String[] mainFileExtensions;

    public static List<GroupedContributionEntity> of(final Stream<Contribution> contributions) {
        final var groupedContributionsPerIssueIdOrPullRequestIdOrCodeReviewId = contributions
                .collect(groupingBy(contribution -> {
                    if (contribution.getPullRequest() != null) {
                        return contribution.getPullRequest().getId().toString();
                    } else if (contribution.getIssue() != null) {
                        return contribution.getIssue().getId().toString();
                    } else if (contribution.getCodeReview() != null) {
                        return contribution.getCodeReview().getId();
                    } else {
                        throw new IllegalArgumentException("Contribution must have a pull request, issue or code review");
                    }
                }));

        return groupedContributionsPerIssueIdOrPullRequestIdOrCodeReviewId.values().stream().map((groupedContributions) -> {
            final var contribution = groupedContributions.get(0);
            final var contributors = groupedContributions.stream()
                    .map(Contribution::getContributor)
                    .filter(Objects::nonNull)
                    .map(GithubAccountEntity::of)
                    .collect(Collectors.toSet());

            final var repo = Optional.ofNullable(contribution.getPullRequest()).map(GithubPullRequest::getRepo)
                    .or(() -> Optional.ofNullable(contribution.getIssue()).map(GithubIssue::getRepo))
                    .or(() -> Optional.ofNullable(contribution.getCodeReview()).map(GithubCodeReview::getPullRequest).map(GithubPullRequest::getRepo));

            final var author = Optional.ofNullable(contribution.getPullRequest()).map(GithubPullRequest::getAuthor)
                    .or(() -> Optional.ofNullable(contribution.getIssue()).map(GithubIssue::getAuthor))
                    .or(() -> Optional.ofNullable(contribution.getCodeReview()).map(GithubCodeReview::getAuthor));

            final var contributor = Optional.ofNullable(contribution.getContributor()).filter(c -> c.getId() != null);

            return GroupedContributionEntity.builder()
                    .repo(GithubRepoEntity.of(contribution.getRepo()))
                    .contributors(contributors)
                    .type(ContributionEntity.Type.of(contribution.getType()))
                    .status(ContributionEntity.Status.of(contribution.getStatus()))
                    .pullRequest(contribution.getPullRequest() != null ? GithubPullRequestEntity.of(contribution.getPullRequest()) : null)
                    .issue(contribution.getIssue() != null ? GithubIssueEntity.of(contribution.getIssue()) : null)
                    .codeReview(contribution.getCodeReview() != null ? GithubCodeReviewEntity.of(contribution.getCodeReview()) : null)
                    .createdAt(contribution.getCreatedAt())
                    .updatedAt(contribution.getUpdatedAt())
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
                    .prReviewState(Optional.ofNullable(contribution.getPullRequest()).map(GithubPullRequest::getReviewState).map(GithubPullRequestEntity.ReviewState::of).orElse(null))
                    .mainFileExtensions(Optional.ofNullable(contribution.getPullRequest()).map(pr -> pr.getMainFileExtensions().toArray(String[]::new)).orElse(null))
                    .build();
        }).toList();

    }
}
