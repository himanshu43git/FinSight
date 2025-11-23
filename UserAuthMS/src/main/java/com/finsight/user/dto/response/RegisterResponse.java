package com.finsight.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse {

    private String userId;
    private String email;
    private String name;
    private String locale;
    private String defaultCurrency;
    private LocalDateTime createdAt;
    private String role;

}
