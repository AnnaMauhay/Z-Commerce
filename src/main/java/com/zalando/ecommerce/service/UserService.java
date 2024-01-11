package com.zalando.ecommerce.service;

import com.zalando.ecommerce.dto.UserRegistrationRequest;
import com.zalando.ecommerce.dto.UserRegistrationResponse;
import com.zalando.ecommerce.exception.DuplicateUserException;
import com.zalando.ecommerce.exception.UserNotFoundException;
import com.zalando.ecommerce.model.User;
import com.zalando.ecommerce.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service @RequiredArgsConstructor
public class UserService implements UserDetailsService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getUserByEmail(String email) throws UserNotFoundException {
        Optional<User> optionalUser = userRepository.getUserByEmail(email);
        if (optionalUser.isPresent()) return optionalUser.get();
        else throw new UserNotFoundException("The provided email does not match any registered user.");
    }

    public boolean isEmailRegistered(String email)  {
        Optional<User> optionalUser = userRepository.getUserByEmail(email);
        return optionalUser.isPresent();
    }

    public UserRegistrationResponse registerUser(UserRegistrationRequest request) throws DuplicateUserException{
        if (isEmailRegistered(request.getEmail())){
            throw new DuplicateUserException("Email is already registered.");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        User savedUser = userRepository.save(user);

        return new UserRegistrationResponse(
                savedUser.getUserId(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getEmail(),
                savedUser.getRole());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User appUser = userRepository.getUserByEmail(username).get();
        return new org.springframework.security.core.userdetails.User(
                appUser.getEmail(), appUser.getPassword(), new ArrayList<>());
    }
}
