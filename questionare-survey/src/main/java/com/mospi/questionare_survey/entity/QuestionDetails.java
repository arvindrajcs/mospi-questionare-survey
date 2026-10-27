package com.mospi.questionare_survey.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "question_details",
        indexes = {
                @Index(name = "idx_question_details_block", columnList = "block_id"),
        }
)
@Getter
@Setter
@ToString // No old-style exclude parameters here
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id", updatable = false, nullable = false)
    private Long questionId;

    // Many questions belong to one Survey Details record
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "survey_id", nullable = false)
    private SurveyDetails survey;

    // Many questions belong to one Block Details record
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "block_id", nullable = false)
    private BlockDetails block;

    @Column(name = "question_number", nullable = false)
    private String questionNumber;

    @Column(name = "field_instructions", nullable = true)
    private String fieldInstructions;

    @Column(name = "data_formate", nullable = true)
    private String dataFormate;

    @Column(name = "drop_down_value", columnDefinition = "TEXT")
    private String dropDownValue;

    @Column(name = "question_heading", nullable = false)
    private String questionHeading;

    @Builder.Default
    @Column(name = "is_mandatory", nullable = false)
    private boolean isMandatory = true;

    @Column(name = "question_condition")
    private String questionCondition;

    // --- ADD THESE TWO MAPPINGS ---
    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SkipCondition> skipConditions = new ArrayList<>();

    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<QuestionValidation> validations = new ArrayList<>();

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