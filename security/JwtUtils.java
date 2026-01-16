package com.digiTicket.backend.security;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtUtils {
    private String jwtSecret = "maCleSecreteTresLongueEtSecuriseePourLeProjetUCAO";
    private int jwtExpirationMs = 86400000; // 24 heures

    // Méthode pour générer le Token après le login
    public String generateJwtToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    // Méthode pour vérifier si un Token est encore valide
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
