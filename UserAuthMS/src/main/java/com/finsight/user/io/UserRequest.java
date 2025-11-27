package com.auction.auth.io;

import com.auction.auth.model.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for create/update user requests.
 * Keeps validation here; hashing and business checks belong to service layer.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRequest {

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    /**
     * Example values: "ROLE_USER", "ROLE_ADMIN".
     */
    private String role;

    private String firstName;

    private String lastName;

    /**
     * Convert this request DTO into a User entity.
     * Important: do not persist the returned User directly if you haven't hashed the password.
     */
    public User toUser() {
        return User.builder()
                .username(this.email) // default username to email if none provided
                .email(this.email)
                .password(this.password) // remember: hash before saving
                .role(this.role == null ? "ROLE_USER" : this.role)
                .firstName(this.firstName)
                .lastName(this.lastName)
                .build();
    }
}
