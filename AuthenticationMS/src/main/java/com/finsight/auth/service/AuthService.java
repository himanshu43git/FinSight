package com.finsight.auth.service;

import com.finsight.auth.io.request.RegisterRequest;
import com.finsight.auth.io.response.RegisterResponse;

import java.util.Optional;

public interface AuthService {

    Optional<RegisterResponse> registerUser(RegisterRequest request);

}
