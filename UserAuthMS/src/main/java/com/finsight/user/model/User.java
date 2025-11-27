package com.auction.auth.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "password")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    private String firstName;
    private String lastName;
    private String role;
    private String avatarUrl;

    @Column(columnDefinition = "text")
    private String preferences;

    @Column(name = "verify_otp")
    private String verifyOtp;

    @Column(name = "is_account_verified")
    private Boolean isAccountVerified;

    @Column(name = "verify_otp_expire_at")
    private Long verifyOtpExpireAt;

    @Column(name = "reset_otp")
    private String resetOtp;

    @Column(name = "reset_otp_expire_at")
    private Long resetOtpExpireAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
