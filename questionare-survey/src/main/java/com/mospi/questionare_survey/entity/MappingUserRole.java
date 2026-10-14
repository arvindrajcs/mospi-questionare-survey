package com.mospi.questionare_survey.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "mapping_user_role",
        indexes = {
                @Index(name = "idx_mapping_user_role_user", columnList = "user_id"),
                @Index(name = "idx_mapping_user_role_role", columnList = "role_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_mapping_user_role", columnNames = {"user_id", "role_id"})
        }
)
@Getter
@Setter
@ToString(exclude = {"user", "role"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MappingUserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_role_mapping_id", updatable = false, nullable = false)
    private Long userRoleMappingId;

    // Many mappings belong to one User
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserInfo user;

    // Many mappings belong to one Role
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private MasRole role;

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