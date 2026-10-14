package com.mospi.questionare_survey.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;

import java.time.OffsetDateTime;
import java.util.Collection;

@Entity
@Table(name = "user_details", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_details_username", columnNames = {"username"}),
        @UniqueConstraint(name = "uk_user_details_email", columnNames = {"email"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Column(name = "password_hash", length = 500)
    private String passwordHash;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "mobile_number", length = 20)
    private String mobileNumber;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "password_expiry_at")
    private OffsetDateTime passwordExpiryAt;

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

    /* ================= SPRING SECURITY METHODS ================= */

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }


    public String getUsername() {
        return this.username;
    }
    public String getName() {
        return this.firstName;
    }


    public boolean isAccountNonExpired() {
        return true;
    }


    public boolean isAccountNonLocked() {
        return true;
    }


    public boolean isCredentialsNonExpired() {
        return true;
    }

    public boolean isEnabled() {
        return true;
    }
}