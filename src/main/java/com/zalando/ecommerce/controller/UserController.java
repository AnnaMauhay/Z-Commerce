package com.zalando.ecommerce.controller;

import com.zalando.ecommerce.dto.UserRegistrationRequest;
import com.zalando.ecommerce.dto.UserRegistrationResponse;
import com.zalando.ecommerce.exception.DuplicateUserException;
import com.zalando.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = {"application/json", "text/xml"})
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationRequest registrationRequest){
        try{
            UserRegistrationResponse response = userService.registerUser(registrationRequest);
            return ResponseEntity.ok(response);
        } catch (DuplicateUserException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
        }
    }
}
