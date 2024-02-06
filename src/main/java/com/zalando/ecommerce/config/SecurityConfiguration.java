package com.zalando.ecommerce.config;

import com.zalando.ecommerce.filter.JwtRequestFilter;
import com.zalando.ecommerce.model.Role;
import com.zalando.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final AuthenticationConfiguration authConfiguration;
    private final UserService userService;
    private final JwtRequestFilter jwtRequestFilter;

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authConfiguration.getAuthenticationManager();
    }

    @Autowired
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> {
                            auth
                                    .requestMatchers("/*/register", "/*/login", "/*/error")
                                    .permitAll();
                            auth
                                    .requestMatchers(
                                            "/*/api-docs",
                                            "/*/api-docs/**",
                                            "/swagger-resources",
                                            "/swagger-resources/**",
                                            "/configuration/ui",
                                            "/configuration/security",
                                            "/swagger-ui/**",
                                            "/swagger-ui/index.html")
                                    .permitAll();
                            auth
                                    .requestMatchers(toH2Console())
                                    .permitAll();
                            auth
                                    .requestMatchers(HttpMethod.GET, "/products", "/products/**")
                                    .permitAll();
                            auth
                                    .requestMatchers(HttpMethod.POST, "/products")
                                    .hasRole(Role.SELLER.name())

                                    .requestMatchers(HttpMethod.PUT, "/products/**")
                                    .hasRole(Role.SELLER.name())

                                    .requestMatchers(HttpMethod.DELETE, "/products/**")
                                    .hasRole(Role.SELLER.name());
                            auth
                                    .requestMatchers(HttpMethod.GET, "/carts")
                                    .hasRole(Role.CUSTOMER.name())

                                    .requestMatchers(HttpMethod.POST, "/carts")
                                    .hasRole(Role.CUSTOMER.name())

                                    .requestMatchers(HttpMethod.PUT, "/carts/**")
                                    .hasRole(Role.CUSTOMER.name())

                                    .requestMatchers(HttpMethod.DELETE, "/carts/**")
                                    .hasRole(Role.CUSTOMER.name());

                            auth
                                    .anyRequest()
                                    .authenticated();
                        }
                )
                .csrf(CsrfConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


}