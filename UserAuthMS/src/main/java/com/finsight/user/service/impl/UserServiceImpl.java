package com.finsight.user.service.impl;

import com.finsight.user.dto.request.RegisterRequest;
import com.finsight.user.dto.response.RegisterResponse;
import com.finsight.user.exception.InvalidCredentialsException;
import com.finsight.user.exception.UserAlreadyExist;
import com.finsight.user.repository.UserRepository;
import com.finsight.user.service.UserService;

import java.util.Optional;

public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public Optional<RegisterResponse> createUser(RegisterRequest request) throws UserAlreadyExist {

        if(userRepository.existByEmail(request.getEmail())){
            throw new UserAlreadyExist("EXCEPTION -->>> USER ALREADY EXIST BY EMAIL OR ID");
        }

        if(request.getEmail().isEmpty() || request.getPassword().isEmpty()){
            throw new InvalidCredentialsException("EXCEPTION -->>> EMAIL OR PASSWORD IS NOT PROVIDED");
        }

        return Optional.empty();
    }
}
