package com.onlydust.marketplace.indexer.postgres.entities.exposition;

import com.onlydust.marketplace.indexer.domain.models.exposition.GithubCodeReview;
import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "github_code_reviews", schema = "indexer_exp")
@TypeDef(name = "github_code_review_state", typeClass = PostgreSQLEnumType.class)
public class GithubCodeReviewEntity {
    @Id
    String id;

    @ManyToOne(cascade = CascadeType.ALL)
    GithubPullRequestEntity pullRequest;

    @ManyToOne(cascade = CascadeType.ALL)
    GithubAccountEntity author;

    @Enumerated(EnumType.STRING)
    @Type(type = "github_code_review_state")
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
