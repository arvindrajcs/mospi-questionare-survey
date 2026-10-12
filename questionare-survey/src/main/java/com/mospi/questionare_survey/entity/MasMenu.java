package com.mospi.questionare_survey.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.OffsetDateTime;

@Entity
@Table(
        name = "mst_menu",
        indexes = {
                @Index(name = "idx_mst_menu_parent", columnList = "parent_id"),
                @Index(name = "idx_mst_menu_order", columnList = "menu_order")
        }
)
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MasMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id", updatable = false, nullable = false)
    private Long menuId;

    @Column(name = "parent_id")
    private String parentId;

    @Column(name = "child_id")
    private String childId;

    @Column(name = "menu_url", length = 500)
    private String menuUrl;

    @Column(name = "icon_name", length = 100)
    private String iconName;

    @Column(name = "label_name", nullable = false, length = 100)
    private String labelName;

    @Column(name = "menu_type", nullable = false, length = 30)
    private String menuType;

    @Column(name = "menu_order", nullable = false)
    private Integer menuOrder;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @JsonIgnore
    @Column(name = "created_by")
    private Long createdBy;

    @JsonIgnore
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @JsonIgnore
    @Column(name = "updated_by")
    private Long updatedBy;

    @JsonIgnore
    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}