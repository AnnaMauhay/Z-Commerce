package com.zalando.ecommerce.model;

import lombok.*;
import org.springframework.http.HttpStatusCode;

import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class ErrorResponse {
    private LocalDateTime createdAt;
    private HttpStatusCode statusCode;
    private String body;

    public ErrorResponse(HttpStatusCode statusCode, String body) {
        this.createdAt = LocalDateTime.now();
        this.statusCode = statusCode;
        this.body = body;
    }
}
