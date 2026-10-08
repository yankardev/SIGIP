package pe.edu.cibertec.sigip.catalogservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.List;

@Service
public class JwtService {

    private final SecretKey key;

    public JwtService(@Value("${sigip.jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String obtenerUsername(String token) {
        return obtenerClaims(token).getSubject();
    }

    public List<String> obtenerAuthorities(String token) {
        Object value = obtenerClaims(token).get("authorities");

        if (value instanceof List<?> list) {
            return list.stream()
                    .map(String::valueOf)
                    .toList();
        }

        return List.of();
    }

    public boolean tokenValido(String token) {
        try {
            Claims claims = obtenerClaims(token);
            return claims.getExpiration() != null
                    && claims.getExpiration().getTime() > System.currentTimeMillis();
        } catch (Exception e) {
            return false;
        }
    }

    private Claims obtenerClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
