package com.zalando.ecommerce.controller;

import com.zalando.ecommerce.dto.*;
import com.zalando.ecommerce.exception.DuplicateUserException;
import com.zalando.ecommerce.exception.InvalidEmailVerificationTokenException;
import com.zalando.ecommerce.exception.UserNotFoundException;
import com.zalando.ecommerce.model.EmailVerificationToken;
import com.zalando.ecommerce.model.ErrorResponse;
import com.zalando.ecommerce.model.User;
import com.zalando.ecommerce.service.EmailTokenService;
import com.zalando.ecommerce.service.UserService;
import com.zalando.ecommerce.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final EmailTokenService emailTokenService;


    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = {"application/json", "text/xml"})
    public ResponseEntity<?> registerUser(@Validated @RequestBody UserRegistrationRequest registrationRequest){
        try{
            UserRegistrationResponse response = userService.registerUser(registrationRequest);
            return ResponseEntity.ok(response);
        } catch (DuplicateUserException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
        }
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@Validated @RequestBody UserLoginRequest loginRequest){
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

    @PostMapping(value = "/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token){
        try{
            User user = emailTokenService.validateToken(token);
            MessageResponse response = new MessageResponse(user.getEmail() + " has been successfully verified.");
            return ResponseEntity.ok(response);
        } catch (InvalidEmailVerificationTokenException e) {
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_ACCEPTABLE, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(errorResponse);
        }
    }
}
