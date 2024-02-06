package com.zalando.ecommerce.config;

import com.github.javafaker.Faker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;
import java.util.Random;

@Configuration
public class SampleDataConfiguration {
    @Bean
    public Faker faker(){
        return new Faker(new Locale("en-US"));
    }

    @Bean
    public Random random(){
        return new Random();
    }
}
