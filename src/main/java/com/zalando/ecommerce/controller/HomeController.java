package com.zalando.ecommerce.controller;

import com.nimbusds.jose.shaded.gson.JsonObject;
import com.zalando.ecommerce.dto.WelcomeResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class HomeController {
    @GetMapping("/welcome")
    public ResponseEntity<?> welcomeMessage(){
        WelcomeResponse response = new WelcomeResponse("Welcome to Zalando E-commerce!");
        return ResponseEntity.ok(response);
    }
}
