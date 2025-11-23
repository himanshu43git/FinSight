package com.finsight.user.service;

import com.finsight.user.dto.request.RegisterRequest;
import com.finsight.user.dto.response.RegisterResponse;
import com.finsight.user.exception.InvalidCredentialsException;
import com.finsight.user.exception.UserAlreadyExist;

import java.util.Optional;

public interface UserService {

    Optional<RegisterResponse> createUser(RegisterRequest request) throws UserAlreadyExist, InvalidCredentialsException;

}
