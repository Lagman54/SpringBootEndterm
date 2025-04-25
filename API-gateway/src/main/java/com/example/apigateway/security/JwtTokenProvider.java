package com.example.apigateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {
    @Value("${app.jwt.secret}")
    private String secretKey;

    @Value("${app.jwt.expire-length-milliseconds}")
    private long expireLengthMilliseconds;

    public String createToken(String username, String customerId) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("customerId", customerId);

        Date now = new Date();
        Date expiry = new Date(now.getTime() + expireLengthMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getCustomerId(String token) {
        return ((Claims) Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody())
                .getSubject();
    }

    public String resolveToken(HttpServletRequest req) {
        String bearer = req.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public long getValidityInMilliseconds() {
        return expireLengthMilliseconds;
    }

    public void setValidityInMilliseconds(long validityInMilliseconds) {
        this.expireLengthMilliseconds = validityInMilliseconds;
    }
}