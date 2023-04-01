package internship.paymentSystem.backend.jwt;

import java.util.Date;

import internship.paymentSystem.backend.config.MyLogger;
import internship.paymentSystem.backend.services.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;

@Component
public class JwtUtils {
    private final MyLogger LOGGER = MyLogger.getInstance();

    @Value("${bezkoder.app.jwtSecret}")
    private String jwtSecret;

    @Value("${bezkoder.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        return Jwts.builder().setSubject((userPrincipal.getUsername())).setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)).signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            LOGGER.logError("Invalid JWT signature: "+ e.getMessage());
        } catch (MalformedJwtException e) {
            LOGGER.logError("Invalid JWT token: "+ e.getMessage());
        } catch (ExpiredJwtException e) {
            LOGGER.logError("JWT token is expired: "+ e.getMessage());
        } catch (UnsupportedJwtException e) {
            LOGGER.logError("JWT token is unsupported: "+ e.getMessage());
        } catch (IllegalArgumentException e) {
            LOGGER.logError("JWT claims string is empty: "+ e.getMessage());
        }
        return false;
    }
}