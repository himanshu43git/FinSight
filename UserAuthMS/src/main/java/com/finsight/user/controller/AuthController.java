package com.finsight.user.controller;


import com.finsight.user.dto.request.RegisterRequest;
import com.finsight.user.dto.response.RegisterResponse;
import com.finsight.user.exception.InvalidCredentialsException;
import com.finsight.user.exception.UserAlreadyExist;
import com.finsight.user.service.UserService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService){
        this.userService = userService;
    }

    public RegisterResponse registerUser(@RequestBody RegisterRequest request) throws InvalidCredentialsException, UserAlreadyExist {

        return userService.createUser(request)
                .orElseThrow(() -> new RuntimeException("User creation failed"));

    }

}
