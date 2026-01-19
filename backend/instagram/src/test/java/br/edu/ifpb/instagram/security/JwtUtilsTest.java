package br.edu.ifpb.instagram.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private final JwtUtils jwtUtils = new JwtUtils();

    @Test
    void shouldGenerateAndValidateToken() {

        Authentication auth =
                new UsernamePasswordAuthenticationToken("lucas", "123");

        String token = jwtUtils.generateToken(auth);

        assertNotNull(token);
        assertTrue(jwtUtils.validateToken(token));
        assertEquals("lucas", jwtUtils.getUsernameFromToken(token));
    }

    @Test
    void shouldReturnFalseForInvalidToken() {

        String invalidToken = "token.invalido.qualquer";

        boolean valid = jwtUtils.validateToken(invalidToken);

        assertFalse(valid);
    }
}
