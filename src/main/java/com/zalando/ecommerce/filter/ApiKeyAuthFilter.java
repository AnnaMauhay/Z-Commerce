package com.zalando.ecommerce.filter;

import com.nimbusds.jose.shaded.gson.JsonObject;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {
    @Value("${api.key}")
    private String apiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestApiKey = request.getHeader("X-API-KEY");
        if (apiKey.equals(requestApiKey)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            JsonObject json = new JsonObject();
            json.addProperty("createdAt", LocalDateTime.now().toString());
            json.addProperty("statusCode", HttpStatus.UNAUTHORIZED.value());
            json.addProperty("body", "Unauthorized. API Key was invalid or not provided.");

            response.getWriter().write(json.toString());
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        List<String> pathList = List.of(
                "/error",
                "/api-docs",
                "/swagger-resources",
                "/configuration/ui",
                "/configuration/security",
                "/swagger-ui",
                "/h2-console");

        for (String item : pathList) {
            if (path.contains(item)) {
                return true;
            }
        }
        return false;
    }
}