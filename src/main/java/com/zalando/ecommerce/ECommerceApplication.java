package com.zalando.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.zalando.ecommerce.repository")
public class ECommerceApplication{

    public static void main(String[] args) {
        SpringApplication.run(ECommerceApplication.class, args);
    }

}
