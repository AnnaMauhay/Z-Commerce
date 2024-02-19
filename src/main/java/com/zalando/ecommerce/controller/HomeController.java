package com.zalando.ecommerce.controller;

import com.zalando.ecommerce.dto.MessageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class HomeController {
    @GetMapping("/welcome")
    public ResponseEntity<?> welcomeMessage(){
        MessageResponse response = new MessageResponse("Welcome to Zalando E-commerce!");
        return ResponseEntity.ok(response);
    }
}
