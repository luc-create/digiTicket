package com.digiTicket.backend.web;

import com.digiTicket.backend.models.User;
import com.digiTicket.backend.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Note : Normalement on utilise un Service et un Repository ici
    // Mais pour ton test auth, voici la logique de base :
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        // 1. Ici le système devrait chercher l'utilisateur en base de données
        // 2. Vérifier le mot de passe avec passwordEncoder.matches(password, user.getPassword())
        
        // 3. Si c'est bon, on génère le token
        String jwt = jwtUtils.generateJwtToken(email);

        Map<String, String> response = new HashMap<>();
        response.put("token", jwt);
        response.put("type", "Bearer");

        return ResponseEntity.ok(response);
    }
}
