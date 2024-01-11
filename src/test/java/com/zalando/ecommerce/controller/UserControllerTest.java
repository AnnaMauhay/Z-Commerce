package com.zalando.ecommerce.controller;

import com.zalando.ecommerce.dto.UserRegistrationRequest;
import com.zalando.ecommerce.dto.UserRegistrationResponse;
import com.zalando.ecommerce.exception.DuplicateUserException;
import com.zalando.ecommerce.model.Role;
import com.zalando.ecommerce.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class UserControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    UserService userService;

    @Autowired
    private MockMvc mockMvc;
    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test @Disabled("Not yet implemented properly.")
    void registerUser() {
    }
    @Test @Disabled("Not yet implemented properly.")
    void registerNewUser_returnOkStatus() throws Exception, DuplicateUserException {
        UserRegistrationResponse response = new UserRegistrationResponse(
                101,"Seller","SellerLastName","seller@gmail.com", Role.SELLER);
        when(userService.registerUser(any(UserRegistrationRequest.class))).thenReturn(response);

        String requestBody = "{\"firstName\": \"Seller\"," +
                "\"lastName\": \"SellerLastName\"," +
                "\"email\": \"seller@gmail.com\"," +
                "\"password\": \"testPass\"" +
                "\"role\": \"SELLER\"}";
        MockHttpServletRequestBuilder requestBuilder = post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .characterEncoding("utf-8");

        this.mockMvc.perform(requestBuilder)
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("seller@gmail.com")));
    }
}