package io.johnathanluong.ecommerce.api;

import io.johnathanluong.ecommerce.api.security.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private String secretKey = "supersecuresecretkeynoteverhackabletypeshittttttt";

    @Mock
    private Authentication mockAuthentication;
    @Mock
    private UserDetails mockUserDetails;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", secretKey);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationInMs", 3600000);
        when(mockAuthentication.getName()).thenReturn("testUser"); 
        when(mockUserDetails.getUsername()).thenReturn("testUser");
    }

    @Test
    void generateToken_ValidAuthentication_ReturnsNonEmptyToken() {
        String token = jwtTokenProvider.generateToken(mockAuthentication);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void getUsernameFromJwt_ValidToken_ReturnsCorrectUsername() {
        String token = jwtTokenProvider.generateToken(mockAuthentication);
        Claims claims = Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        Date expirationDate = claims.getExpiration();
        System.out.println("Token Expiration Time: " + expirationDate); // Print expiration time
        String username = jwtTokenProvider.getUsernameFromJwt(token);
        assertEquals("testUser", username);
    }

    @Test
    void validateToken_ValidToken_ReturnsTrue() {
        String token = jwtTokenProvider.generateToken(mockAuthentication);
        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    void validateToken_ExpiredToken_ReturnsFalse() {
        String expiredToken = Jwts.builder()
                .subject("testUser")
                .issuedAt(new Date(System.currentTimeMillis() - 86400000))
                .expiration(new Date(System.currentTimeMillis() - 3600000)) 
                .signWith(getSignInKey())
                .compact();
        assertFalse(jwtTokenProvider.validateToken(expiredToken));
    }

    @Test
    void validateToken_MalformedToken_ReturnsFalse() {
        String malformedToken = "malformed.jwt.token";
        assertFalse(jwtTokenProvider.validateToken(malformedToken));
    }

    @Test
    void validateToken_TamperedSignature_ReturnsFalse() {
        String validToken = jwtTokenProvider.generateToken(mockAuthentication);
        String[] parts = validToken.split("\\.");
        String tamperedToken = parts[0] + "." + parts[1] + ".tamperedSignature";
        assertFalse(jwtTokenProvider.validateToken(tamperedToken));
    }

    private SecretKey getSignInKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }
}