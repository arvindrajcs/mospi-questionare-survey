package com.mospi.questionare_survey.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "REFRESH_TOKEN")
public class RefreshToken {

//    @JsonIgnore
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;

    @JsonIgnore
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "refresh_token_seq_gen"
    )
    @SequenceGenerator(
            name = "refresh_token_seq_gen",
            sequenceName = "REFRESH_TOKEN_SEQ",
            allocationSize = 1
    )
    @Column(name = "ID")
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = true)
    private String username;

    @Column(nullable = false)
    private String ipAddress;

    @Column(nullable = false, unique = true)
    private String tokenHash; // store a hash of the refresh token

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = true)
    private String deviceInfo; // optional: tie to device or IP

    @JsonIgnore
    private boolean revoked = false;

}
