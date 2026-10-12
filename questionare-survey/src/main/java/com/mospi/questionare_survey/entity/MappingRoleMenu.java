package com.mospi.questionare_survey.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "mapping_role_menu",
        indexes = {
                @Index(name = "idx_mapping_role_menu_role", columnList = "role_id"),
                @Index(name = "idx_mapping_role_menu_menu", columnList = "menu_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_mapping_role_menu", columnNames = {"role_id", "menu_id"})
        }
)
@Getter
@Setter
@ToString(exclude = {"role", "menu"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MappingRoleMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_menu_mapping_id", updatable = false, nullable = false)
    private Long roleMenuMappingId;

    // Many mappings belong to one Role
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private MasRole role;

    // Many mappings belong to one Menu
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "menu_id", nullable = false)
    private MasMenu menu;

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