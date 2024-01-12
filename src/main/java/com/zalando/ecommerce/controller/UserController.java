package com.zalando.ecommerce.controller;

import com.zalando.ecommerce.dto.AuthenticationResponse;
import com.zalando.ecommerce.dto.UserLoginRequest;
import com.zalando.ecommerce.dto.UserRegistrationRequest;
import com.zalando.ecommerce.dto.UserRegistrationResponse;
import com.zalando.ecommerce.exception.DuplicateUserException;
import com.zalando.ecommerce.service.UserService;
import com.zalando.ecommerce.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;


    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = {"application/json", "text/xml"})
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationRequest registrationRequest){
        try{
            UserRegistrationResponse response = userService.registerUser(registrationRequest);
            return ResponseEntity.ok(response);
        } catch (DuplicateUserException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
        }
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest loginRequest){
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
            ));
            final String jwt = jwtUtil.generateToken(loginRequest.getEmail());

            return ResponseEntity.ok(new AuthenticationResponse(jwt));
        }catch (AuthenticationException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username and/or password.");
        }
    }
}
