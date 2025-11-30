package com.finsight.auth.controller;

import com.finsight.auth.io.UserRequest;
import com.finsight.auth.io.UserResponse;
import com.finsight.auth.services.UserService;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public UserResponse createUser(@RequestBody @Valid UserRequest userRequest){
        return userService.createUser(userRequest)
                .orElseThrow(() -> new RuntimeException("User creation failed"));

    }


}
