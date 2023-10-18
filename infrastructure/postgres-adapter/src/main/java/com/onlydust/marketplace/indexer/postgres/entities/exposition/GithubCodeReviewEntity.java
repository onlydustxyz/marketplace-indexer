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

    @ManyToOne
    GithubPullRequestEntity pullRequest;

    @ManyToOne
    GithubAccountEntity author;

    @Enumerated(EnumType.STRING)
    @Type(type = "github_code_review_state")
    State state;
    Date requestedAt;
    Date submittedAt;

    public static GithubCodeReviewEntity of(GithubCodeReview codeReview) {
        return GithubCodeReviewEntity.builder()
                .id(codeReview.getId())
                .pullRequest(GithubPullRequestEntity.of(codeReview.getPullRequest()))
                .author(GithubAccountEntity.of(codeReview.getAuthor()))
                .state(State.of(codeReview.getState()))
                .requestedAt(codeReview.getRequestedAt())
                .submittedAt(codeReview.getSubmittedAt())
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
