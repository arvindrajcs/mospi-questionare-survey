package com.mospi.questionare_survey.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "survey_details",
        indexes = {
                @Index(name = "idx_survey_details_code", columnList = "survey_code"),
                @Index(name = "idx_survey_details_type", columnList = "survey_type_id"),
                @Index(name = "idx_survey_details_division", columnList = "division_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_survey_code", columnNames = {"survey_code"})
        }
)
@Getter
@Setter
@ToString(exclude = {"surveyType", "division"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "survey_id", updatable = false, nullable = false)
    private Long surveyId;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    // Many surveys belong to one Survey Type
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "survey_type_id", nullable = false)
    private MasSurveyType surveyType;

    @Column(name = "survey_code", nullable = false, length = 100)
    private String surveyCode;

    // Many surveys belong to one Division
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "division_id", nullable = false)
    private MasDivision division;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "template_name", length = 100)
    private String templateName;

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