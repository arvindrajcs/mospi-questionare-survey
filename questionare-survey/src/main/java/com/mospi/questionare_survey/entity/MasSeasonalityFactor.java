package com.mospi.questionare_survey.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "mst_seasonality_factor",
        indexes = {
                @Index(name = "idx_mst_seasonality_factor_description", columnList = "description")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_mst_seasonality_factor_description", columnNames = {"description"})
        }
)
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasSeasonalityFactor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seasonality_factor_id", updatable = false, nullable = false)
    private Long seasonalityFactorId;

    @Column(name = "description", length = 500)
    private String description;

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