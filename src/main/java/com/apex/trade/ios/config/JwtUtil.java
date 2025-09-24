package com.apex.trade.ios.config;

import com.apex.trade.ios.registration.entities.Investor;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JwtUtil {
    private final String jwtSecret = "uW8vJt6mRzQf4B1KxP9YdM2HsVcXtZeFqLnOwEjRgUkTiYpX";
    private final int jwtExpirationMs = 86400000;

    public String generateToken(UserDetails userDetails,Investor investor) {
        log.info("Generating token for {} with PAN: {}", investor.getEmail(), investor.getPanNumber());
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", investor.getEmail());
        claims.put("fullName", investor.getFullName());
        claims.put("phoneNumber", investor.getPhoneNumber());
        claims.put("panNumber", investor.getPanNumber());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername()) // will use Investor.getUsername() → email
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }


    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes())).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            log.error(e.getMessage());
        }
        return false;
    }
    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}

