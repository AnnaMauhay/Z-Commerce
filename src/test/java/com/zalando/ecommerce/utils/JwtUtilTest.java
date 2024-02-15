package com.zalando.ecommerce.utils;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
class JwtUtilTest {
    private JwtUtil jwtUtil;
    private String email;
    private String ISSUER = "Test_Issuer";
    private String SECRET_KEY = "Test_Secret_Key";
    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        jwtUtil.setSECRET_KEY(SECRET_KEY);
        jwtUtil.setISSUER(ISSUER);
        email = "test@email.com";
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testExtractUserName_equalsEmail() {
        String jwt = jwtUtil.generateToken(email);
        assertEquals(email, jwtUtil.extractUserName(jwt));
    }

    @Test
    void testExtractClaim_returnsCorrectValue() {
        String jwt = jwtUtil.generateToken(email);
        assertEquals(ISSUER, jwtUtil.extractClaim(jwt, Claims::getIssuer));
    }

    @Test
    void testExtractExpirationDate_equalsFutureDate() {
        String jwt = jwtUtil.generateToken(email);

        Date jwtExpirationDate = jwtUtil.extractExpirationDate(jwt);

        assertTrue(ChronoUnit.HOURS.between(ZonedDateTime.now().toInstant(),
                jwtExpirationDate.toInstant())>1);
    }

    @Test
    void testGenerateTokenIsNotEmpty() {
        assertFalse(jwtUtil.generateToken(email).isEmpty());
    }

    @Test
    void testValidToken_returnTrueWhenValidated() {
        String jwt = jwtUtil.generateToken(email);
        User user = new User(email,"testPass",new ArrayList<>());

        assertTrue(jwtUtil.validateToken(jwt, user));
    }
}