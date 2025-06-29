package com.learning.services;

import com.learning.data.models.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Method;
import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class JWTServiceTest {

    @Autowired
    private JWTService jwtService;

    @Test
    public void testGenerateAndValidateToken() {
        String email = "test@example.com";
        String token = jwtService.generateToken(email, Role.STUDENT);
        assertNotNull(token);
        assertTrue(jwtService.validateToken(token));
    }

    @Test
    public void testExtractEmailAndRole() {
        String email = "user@example.com";
        String token = jwtService.generateToken(email, Role.LECTURER);
        Claims claims = jwtService.extractAllClaims(token);

        assertEquals(email, claims.getSubject());
        assertEquals("INSTRUCTOR", jwtService.extractRole(token));
        assertEquals("ROLE_INSTRUCTOR", claims.get("role", String.class));
        assertEquals("access", claims.get("type", String.class));
    }

    @Test
    public void testInvalidTokenFailsValidation() {
        String fakeToken = "this.is.not.valid";
        assertFalse(jwtService.validateToken(fakeToken));
    }

    @Test
    public void testExpiredTokenFailsValidation() throws Exception {
        String email = "test@example.com";
        Method getSigningKey = JWTService.class.getDeclaredMethod("getSigningKey");
        getSigningKey.setAccessible(true);
        Key signingKey = (Key) getSigningKey.invoke(jwtService);

        String token = Jwts.builder()
                .setSubject(email)
                .claim("role", "ROLE_" + Role.STUDENT.name())
                .claim("type", "access")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();

        Thread.sleep(2);
        assertFalse(jwtService.validateToken(token));
    }
}