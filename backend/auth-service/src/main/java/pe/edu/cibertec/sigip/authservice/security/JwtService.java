package pe.edu.cibertec.sigip.authservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationMillis;

    public JwtService(
            @Value("${sigip.jwt.secret}") String secret,
            @Value("${sigip.jwt.expiration-minutes:30}") long expirationMinutes
    ) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expirationMillis = expirationMinutes * 60_000L;
    }

    public String generarToken(String username, List<? extends GrantedAuthority> authorities) {
        Instant now = Instant.now();

        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .subject(username)
                .claim("authorities", roles)
                .issuedAt(Date.from(now))
                .expiration(new Date(now.toEpochMilli() + expirationMillis))
                .signWith(key)
                .compact();
    }

    public String obtenerUsername(String token) {
        return obtenerClaims(token).getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> obtenerAuthorities(String token) {
        Object value = obtenerClaims(token).get("authorities");
        if (value instanceof List<?> list) {
            return list.stream()
                    .map(String::valueOf)
                    .toList();
        }
        return List.of();
    }

    public boolean tokenValido(String token, String username) {
        try {
            return username.equals(obtenerUsername(token))
                    && obtenerClaims(token).getExpiration().after(new Date());
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
