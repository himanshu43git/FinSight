package com.finsight.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(unique = true)
    private String phoneNumber;   // used for MFA (optional)

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Boolean enabled = true;    // user enabled/disabled

    @Column(nullable = false)
    private Boolean locked = false;     // account lock after failed attempts

    @Column(nullable = false)
    private Integer failedLoginAttempts = 0;

    private LocalDateTime lastLoginAt;

    private LocalDateTime lastPasswordChangeAt;

    @Column(nullable = false)
    private Boolean twoFactorEnabled = false;

    private String twoFactorSecret; // TOTP secret (if using Google Authenticator)

    // Refresh token storage (optional: for JWT or OAuth)
    private String refreshToken;

    private LocalDateTime refreshTokenExpiry;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Boolean deleted = false;

    private LocalDateTime deletedAt;

    // Roles (Authority-based authentication)
//    @ManyToMany(fetch = FetchType.EAGER)
//    private List<Role> roles;
}
