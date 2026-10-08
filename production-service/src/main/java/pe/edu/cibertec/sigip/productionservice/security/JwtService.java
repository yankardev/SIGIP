package pe.edu.cibertec.sigip.productionservice.security;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.List;
@Service
public class JwtService{
 private final SecretKey key;
 public JwtService(@Value("${sigip.jwt.secret}") String secret){key=Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));}
 public String user(String token){return claims(token).getSubject();}
 public List<String> authorities(String token){Object v=claims(token).get("authorities");return v instanceof List<?> l?l.stream().map(String::valueOf).toList():List.of();}
 public boolean valid(String token){try{Claims c=claims(token);return c.getExpiration()!=null&&c.getExpiration().getTime()>System.currentTimeMillis();}catch(Exception e){return false;}}
 private Claims claims(String token){return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();}
}