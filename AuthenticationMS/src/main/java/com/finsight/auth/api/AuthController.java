package com.finsight.auth.api;

import com.finsight.auth.exception.ApiException;
import com.finsight.auth.io.request.RegisterRequest;
import com.finsight.auth.io.response.RegisterResponse;
import com.finsight.auth.service.AuthService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public RegisterResponse registerUser(RegisterRequest request) {
        return authService.registerUser(request)
                .orElseThrow(() -> new ApiException("Registration failed"));
    }

}
