package cat.ajterrassa.validaciofactures.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    // 🗝️ Clave secreta para firmar el token
    //@Value("${JWT_SECRET}") //no es corrrecto, ya que Spring Boot no reconoce la propiedad con mayúsculas
    //private String secret;
@org.springframework.beans.factory.annotation.Value("${jwt.secret}")
private String secret;

@org.springframework.beans.factory.annotation.Value("${jwt.expiration}")
private long expiration;


    // 🔑 Obtiene la clave de firma a partir del secreto
    //private Key getSigningKey() {
    //    return Keys.hmacShaKeyFor(secret.getBytes());
    //}
    private Key getSigningKey() {
    byte[] keyBytes = secret.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    return io.jsonwebtoken.security.Keys.hmacShaKeyFor(keyBytes); // requereix >= 32 chars
}


    // 🔐 Genera un token para un usuario
    public String generateToken(String username) {
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    // 📤 Extrae el username del token
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // ✅ Valida el token contra un usuario
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    // 📅 Comprueba si el token ha expirado
    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    // 🛠️ Extrae todos los claims del token
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("El token ha expirado", e);
        } catch (UnsupportedJwtException e) {
            throw new RuntimeException("El token no es compatible", e);
        } catch (MalformedJwtException e) {
            throw new RuntimeException("El token está mal formado", e);
        } catch (SignatureException e) {
            throw new RuntimeException("La firma del token no es válida", e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("El token está vacío o es nulo", e);
        }
    }
}