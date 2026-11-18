package com.alten.shop.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final SecretKey key;
    private final Duration ttl;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.ttl-minutes}") long ttlMinutes) {
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) throw new IllegalStateException("app.jwt.secret must be >= 32 bytes (HS256)");
        this.key = Keys.hmacShaKeyFor(bytes);
        this.ttl = Duration.ofMinutes(ttlMinutes);
    }

    public String generate(String email) {
        Date now = new Date();
        return Jwts.builder().subject(email).issuedAt(now)
                .expiration(new Date(now.getTime() + ttl.toMillis()))
                .signWith(key, Jwts.SIG.HS256).compact();
    }

    /** Returns the email (subject) or throws JwtException if invalid/expired. */
    public String validateAndGetEmail(String token) throws JwtException {
        Claims c = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        return c.getSubject();
    }
}
