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
                @Index(name = "idx_survey_details_division", columnList = "division_id"),
                @Index(name = "idx_survey_details_enquiry_type", columnList = "enquiry_type_id"),
                @Index(name = "idx_survey_details_frequency_type", columnList = "frequency_type_id"),
                @Index(name = "idx_survey_details_seasonality_factor", columnList = "seasonality_factor_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_survey_code", columnNames = {"survey_code"})
        }
)
@Getter
@Setter
@ToString
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

    @Column(name = "periodic_cycle", nullable = false, length = 100)
    private String periodicCycle;

    @Column(name = "survey_code", nullable = false, length = 100)
    private String surveyCode;

    // Many surveys belong to one Division
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "division_id", nullable = false)
    private MasDivision division;

    // Many surveys belong to one Enquiry Type
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "enquiry_type_id", nullable = false)
    private MasEnquiryType enquiryType;

    // Many surveys belong to one Frequency Type
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "frequency_type_id", nullable = false)
    private MasFrequencyType frequencyType;

    // Many surveys belong to one Seasonality Factor
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seasonality_factor_id", nullable = false)
    private MasSeasonalityFactor seasonalityFactor;

    @Column(name = "survey_duration", nullable = false, length = 100)
    private String surveyDuration;

    @Column(name = "rural_sampling_frame", nullable = false, length = 100)
    private String ruralSamplingFrame;

    @Column(name = "urban_sampling_frame", nullable = false, length = 100)
    private String urbanSamplingFrame;

    @Column(name = "sampling_size", nullable = false, length = 100)
    private String samplingSize;

    @Column(name = "survey_objective", nullable = false, length = 500)
    private String surveyObjective;

    @Column(name = "remarks", length = 1000)
    private String remarks;

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