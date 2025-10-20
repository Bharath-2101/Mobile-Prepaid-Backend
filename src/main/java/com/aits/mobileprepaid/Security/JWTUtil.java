package com.aits.mobileprepaid.Security;

import com.aits.mobileprepaid.Entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Data
public class JWTUtil {

    @Value("${jwt.secretKey}")
    private String secretKey;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateJWT(User user) {

        long durationInMillis = 86_400_000L;

        return  Jwts.builder()
                    .subject(user.getMobile())
                    .claim("UserId",user.getId())
                    .claim("Role",user.getRole().name())
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis()+durationInMillis))
                    .signWith(getSecretKey())
                    .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims =  Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    public boolean isTokenValid(String token, User user) {
        try {
            String username = getUsernameFromToken(token);
            return username.equals(user.getMobile()) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }


}
