package chattingAPI.backend.Security;

import chattingAPI.backend.entity.Person;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

@Component
public class JwtTokenProvider {

    @Value("${app.key}")
    private  String SECRET_KEY;

    private final Long JWT_EXPIRATION_MS=1000*60*60*24L;

    private Key getSecretKey(){
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Person person){
        Date now =new Date();
        Date expiratonDate=new Date(now.getTime()+JWT_EXPIRATION_MS);
        return Jwts.builder()
                .subject(person.getName())
                .claim("E-mail",person.getMail())
                .expiration(expiratonDate)
                .issuedAt(now)
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    public String getUsernameFromJwtToken(String token){
        Claims claims=Jwts.parser()
                .verifyWith((SecretKey) getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();

    }
    // i m here
    public boolean validateToken(String autotoken){
        try{
            Jwts.parser().verifyWith((SecretKey) getSecretKey()).build().parseSignedClaims(autotoken);
            return true;
        }catch(JwtException | IllegalArgumentException ex){
            return false;
        }
    }
}
