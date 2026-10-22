package com.mospi.questionare_survey.entity; // Update with your actual package

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "question_validation",
        indexes = {
                @Index(name = "idx_question_validation_question", columnList = "question_id")
        }
)
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionValidation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "validation_id", updatable = false, nullable = false)
    private Long validationId;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionDetails question;

    @Column(name = "validation_type", length = 100)
    private String validationType;

    @Column(name = "severity_check", length = 50)
    private String severityCheck;

    @Column(name = "value", length = 255)
    private String value;

    @Column(name = "error_msg", length = 500)
    private String errorMsg;

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