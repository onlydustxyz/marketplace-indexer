package com.onlydust.marketplace.indexer.postgres.entities.exposition;

import com.onlydust.marketplace.indexer.domain.models.exposition.GithubCodeReview;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.util.Date;

@Entity
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Getter
@Table(name = "github_code_reviews", schema = "indexer_exp")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GithubCodeReviewEntity {
    @Id
    String id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    GithubPullRequestEntity pullRequest;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    GithubAccountEntity author;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "github_code_review_state")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    State state;
    Date requestedAt;
    Date submittedAt;
    Long number;
    String title;
    String html_url;
    String body;
    Integer comments_count;
    String repo_owner_login;
    String repo_name;
    Long repoId;
    String repoHtmlUrl;
    String authorLogin;
    String authorHtmlUrl;
    String authorAvatarUrl;

    public static GithubCodeReviewEntity of(GithubCodeReview codeReview) {
        return GithubCodeReviewEntity.builder()
                .id(codeReview.getId())
                .pullRequest(GithubPullRequestEntity.of(codeReview.getPullRequest()))
                .author(GithubAccountEntity.of(codeReview.getAuthor()))
                .state(State.of(codeReview.getState()))
                .requestedAt(codeReview.getRequestedAt())
                .submittedAt(codeReview.getSubmittedAt())
                .number(codeReview.getPullRequest().getNumber())
                .title(codeReview.getPullRequest().getTitle())
                .html_url(codeReview.getPullRequest().getHtmlUrl())
                .body(codeReview.getPullRequest().getBody())
                .comments_count(codeReview.getPullRequest().getCommentsCount())
                .repo_owner_login(codeReview.getPullRequest().getRepo().getOwner().getLogin())
                .repo_name(codeReview.getPullRequest().getRepo().getName())
                .repoId(codeReview.getPullRequest().getRepo().getId())
                .repoHtmlUrl(codeReview.getPullRequest().getRepo().getHtmlUrl())
                .authorLogin(codeReview.getAuthor().getLogin())
                .authorHtmlUrl(codeReview.getAuthor().getHtmlUrl())
                .authorAvatarUrl(codeReview.getAuthor().getAvatarUrl())
                .build();
    }

    public enum State {
        PENDING, COMMENTED, APPROVED, CHANGES_REQUESTED, DISMISSED;

        public static State of(GithubCodeReview.State state) {
            return switch (state) {
                case PENDING -> PENDING;
                case COMMENTED -> COMMENTED;
                case APPROVED -> APPROVED;
                case CHANGES_REQUESTED -> CHANGES_REQUESTED;
                case DISMISSED -> DISMISSED;
            };
        }
    }
}
