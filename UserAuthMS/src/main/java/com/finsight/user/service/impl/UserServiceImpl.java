package com.finsight.user.service.impl;

import com.finsight.user.dto.request.RegisterRequest;
import com.finsight.user.dto.response.RegisterResponse;
import com.finsight.user.entity.User;
import com.finsight.user.exception.InvalidCredentialsException;
import com.finsight.user.exception.UserAlreadyExist;
import com.finsight.user.repository.UserRepository;
import com.finsight.user.service.UserService;

import java.util.Optional;
import java.util.UUID;

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public Optional<RegisterResponse> createUser(RegisterRequest request) throws UserAlreadyExist, InvalidCredentialsException {

        if(userRepository.existByEmail(request.getEmail())){
            throw new UserAlreadyExist("EXCEPTION -->>> USER ALREADY EXIST BY EMAIL OR ID");
        }

        if(request.getEmail().isEmpty() || request.getPassword().isEmpty()){
            throw new InvalidCredentialsException("EXCEPTION -->>> EMAIL OR PASSWORD IS NOT PROVIDED");
        }

        String id = UUID.randomUUID().toString();
        String pass = request.getPassword(); // password will be encoded here

        User user = User.builder()
                .userId(id)
                .email(request.getEmail())
                .hashedPassword(pass)
                .locale(request.getLocale())
                .defaultCurrency(request.getDefaultCurrency())
                .name(request.getName())
                .build();

        user = userRepository.save(user);

        RegisterResponse response = toResponse(user);

        return Optional.of(response);
    }

    private RegisterResponse toResponse(User user){
        return new RegisterResponse(
                user.getUserId(),
                user.getEmail(),
                user.getName(),
                user.getLocale(),
                user.getDefaultCurrency(),
                user.getCreatedAt(),
                user.getRole()
        );

    }
}
