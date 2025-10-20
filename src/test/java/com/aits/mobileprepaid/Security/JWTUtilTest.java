package com.aits.mobileprepaid.Security;

import com.aits.mobileprepaid.Entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JWTUtilTest {

    private JWTUtil jwtUtil;
    private User testUser;

    @BeforeEach
    void setUp() {
        jwtUtil = new JWTUtil();
        ReflectionTestUtils.setField(jwtUtil, "secretKey", "my-secret-key-which-is-long-enough-for-hmacsha");
        testUser = new User();
        testUser.setId(1L);
        testUser.setMobile("123456789");
    }

    @Test
    void itShouldGenerateAToken() {
        String jwt = jwtUtil.generateJWT(testUser);
        assertNotNull(jwt);
        assertFalse(jwt.isEmpty());
    }

    @Test
    void itShouldGiveCorrectUsernameIfTokenIsNotTampered() {
        String jwt = jwtUtil.generateJWT(testUser);
        String userNameFromToken = jwtUtil.getUsernameFromToken(jwt);
        assertNotNull(userNameFromToken);
        assertEquals(testUser.getMobile(), userNameFromToken);
    }

    @Test
    void itShouldNotGiveCorrectUsernameIfTokenIsTampered() {
        String jwt = jwtUtil.generateJWT(testUser);
        String tamperedJwt = jwt.substring(0, jwt.length() - 2) + "ab";

        assertThrows(Exception.class, () -> {
            jwtUtil.getUsernameFromToken(tamperedJwt);
        });
    }

    @Test
    void itShouldGiveTrueIfTokenIsNotTampered() {
        String jwt = jwtUtil.generateJWT(testUser);
        boolean isValid= jwtUtil.isTokenValid(jwt,testUser);
        assertTrue(isValid);
    }

    @Test
    void itShouldGiveFalseIfTokenIsNotTampered() {
        String jwt = jwtUtil.generateJWT(testUser);
        //tampering Token
        String tamperedJwt = jwt.substring(0, jwt.length() - 2) + "ab";
       assertFalse(jwtUtil.isTokenValid(tamperedJwt,testUser));
    }

    @Test
    void itShouldGiveFalseIfTokenIsCheckedWithOtherPerson() {
        String jwt = jwtUtil.generateJWT(testUser);
        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setMobile("9999999999");
        assertFalse(jwtUtil.isTokenValid(jwt,otherUser));
    }
}