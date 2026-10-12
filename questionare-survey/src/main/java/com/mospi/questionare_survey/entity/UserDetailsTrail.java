package com.mospi.questionare_survey.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "user_details_trail",
        indexes = {
                @Index(name = "idx_user_details_trail_user", columnList = "user_id"),
                @Index(name = "idx_user_details_trail_changed_at", columnList = "changed_at")
        }
)
@Getter
@Setter
@ToString(exclude = {"user"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsTrail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trail_id", updatable = false, nullable = false)
    private Long trailId;

    // Many audit trails belong to one User
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private UserDetails user;

    @Column(name = "action_type", nullable = false, length = 50)
    private String actionType;

    @Column(name = "field_name", length = 100)
    private String fieldName;

    // Maps PostgreSQL JSONB to a String column type
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "old_value", columnDefinition = "jsonb")
    private String oldValue;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "new_value", columnDefinition = "jsonb")
    private String newValue;

    // Maps PostgreSQL INET type as a standard String (e.g., "192.168.1.1")
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 1000)
    private String userAgent;

    @Column(name = "remarks", length = 1000)
    private String remarks;

    @Column(name = "changed_by")
    private Long changedBy;

    @CreationTimestamp
    @Column(name = "changed_at", nullable = false, updatable = false)
    private OffsetDateTime changedAt;
}