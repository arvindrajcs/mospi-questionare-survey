package com.mospi.questionare_survey.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "question_details",
        indexes = {
                @Index(name = "idx_question_details_block", columnList = "block_id"),
                @Index(name = "idx_question_details_order", columnList = "display_order")
        }
)
@Getter
@Setter
@ToString(exclude = {"block"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id", updatable = false, nullable = false)
    private Long questionId;

    // Many schedules belong to one Survey Details record
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "survey_id", nullable = false)
    private SurveyDetails survey;

    // Many questions belong to one Block Details record
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "block_id", nullable = false)
    private BlockDetails block;

    @Column(name = "question_number", nullable = false)
    private String questionNumber;

    @Column(name = "field_instructions", nullable = false)
    private String fieldInstructions;

    @Column(name = "question_heading", nullable = false)
    private String questionHeading;

    @Builder.Default
    @Column(name = "display_order", nullable = true)
    private Integer displayOrder = 1;

    @Builder.Default
    @Column(name = "is_mandatory", nullable = false)
    private boolean isMandatory = true;

    // Many surveys belong to one Status
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "status_id", nullable = false)
    private MasStatus status;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "created_by")
    private Long createdBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_by")
    private Long updatedBy;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}