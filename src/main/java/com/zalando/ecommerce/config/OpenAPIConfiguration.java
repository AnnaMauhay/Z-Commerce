package com.zalando.ecommerce.config;

import com.zalando.ecommerce.controller.CartController;
import com.zalando.ecommerce.controller.OrderController;
import com.zalando.ecommerce.controller.ProductController;
import com.zalando.ecommerce.controller.UserController;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
@Configuration
@OpenAPIDefinition
public class OpenAPIConfiguration {
    private final String devUrl = "http://localhost:8080/api/v1";

    @Bean
    public OpenAPI openAPI(){
        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.setDescription("Server URL in Development Environment");

        Contact contact = new Contact();
        contact.setEmail("mauhay.anna.liza@gmail.com");
        contact.setName("Anna Liza Mauhay");

        Info info = new Info()
                .title("E-Commerce API")
                .version("1.0")
                .contact(contact)
                .license(new License().name("Apache 2.0").url("http://springdoc.org"))
                .description("This API exposes endpoints to an e-commerce functionality.");

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("bearer-key", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")))
                .info(info)
                .servers(List.of(devServer));
    }

    static {
        SpringDocUtils.getConfig().addRestControllers(
                UserController.class, ProductController.class, CartController.class, OrderController.class);
    }
}
