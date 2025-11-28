package com.finsight.auth.service.impl;

import com.finsight.auth.io.request.RegisterRequest;
import com.finsight.auth.io.response.RegisterResponse;
import com.finsight.auth.repository.UserRepository;
import com.finsight.auth.service.AuthService;

import java.util.Optional;

public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    public AuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<RegisterResponse> registerUser(RegisterRequest request) {
        return Optional.empty();
    }
}
