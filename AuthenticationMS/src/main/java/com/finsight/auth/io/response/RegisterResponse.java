package com.finsight.auth.io.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RegisterResponse {

    private String userId;
    private String email;
    private String phoneNumber;
    private String message;
    private LocalDateTime createdAt;

}
