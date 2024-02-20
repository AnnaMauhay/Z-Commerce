package com.zalando.ecommerce.controller;

import com.zalando.ecommerce.dto.*;
import com.zalando.ecommerce.exception.DuplicateUserException;
import com.zalando.ecommerce.exception.InvalidEmailVerificationTokenException;
import com.zalando.ecommerce.model.ErrorResponse;
import com.zalando.ecommerce.model.User;
import com.zalando.ecommerce.service.EmailTokenService;
import com.zalando.ecommerce.service.UserService;
import com.zalando.ecommerce.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller("/users")
@RequestMapping//("/users")
@Tag(name = "User", description = "User management APIs")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final EmailTokenService emailTokenService;

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = {"application/json", "text/xml"})
    @Operation(summary = "Register a new user.", description = "Returns the details of the new user.")
    public ResponseEntity<?> registerUser(@Validated @RequestBody UserRegistrationRequest registrationRequest){
        try{
            UserRegistrationResponse response = userService.registerUser(registrationRequest);
            return ResponseEntity.ok(response);
        } catch (DuplicateUserException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
        }
    }

    @PostMapping(value = "/login")
    @Operation(summary = "Authenticate user", description = "Accepts login credentials and returns a JWT.")
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
    @Operation(summary = "Accepts a token received via the user's email", description = "Returns confirmation of user's verified email.")
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
