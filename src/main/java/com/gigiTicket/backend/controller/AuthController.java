package com.gigiTicket.backend.controller;

import com.gigiTicket.backend.exception.ApiException;
import com.gigiTicket.backend.model.Role;
import com.gigiTicket.backend.model.User;
import com.gigiTicket.backend.repository.UserRepository;
import com.gigiTicket.backend.security.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "Endpoints d'authentification (login, register)")
public class AuthController {

	private final UserRepository userRepository;
	private final JwtUtils jwtUtils;
	private final PasswordEncoder passwordEncoder;

	@PostMapping("/login")
	@Operation(summary = "Connexion", description = "Authentifie un utilisateur et retourne un token JWT")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Connexion réussie",
					content = @Content(mediaType = "application/json",
							examples = @ExampleObject(value = "{\n" +
									"  \"token\": \"eyJhbGciOiJIUzUxMiJ9...\",\n" +
									"  \"type\": \"Bearer\",\n" +
									"  \"user\": {\n" +
									"    \"id\": 1,\n" +
									"    \"email\": \"jean.dupont@test.com\",\n" +
									"    \"nom\": \"Jean Dupont\",\n" +
									"    \"role\": \"CLIENT\"\n" +
									"  }\n" +
									"}"))),
			@ApiResponse(responseCode = "400", description = "Email ou mot de passe incorrect")
	})
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "Identifiants de connexion",
			required = true,
			content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = Map.class),
					examples = @ExampleObject(
							name = "Exemple de connexion",
							value = "{\n" +
									"  \"email\": \"jean.dupont@test.com\",\n" +
									"  \"password\": \"password123\"\n" +
									"}"
					)
			)
	)
	public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
		String email = loginRequest.get("email");
		String password = loginRequest.get("password");

		if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
			throw new ApiException("Email et mot de passe sont requis", HttpStatus.BAD_REQUEST);
		}

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ApiException("Email ou mot de passe incorrect", HttpStatus.UNAUTHORIZED));

		if (user.getPassword() == null || !passwordEncoder.matches(password, user.getPassword())) {
			throw new ApiException("Email ou mot de passe incorrect", HttpStatus.UNAUTHORIZED);
		}

		String jwt = jwtUtils.generateJwtToken(email);

		Map<String, Object> response = new HashMap<>();
		response.put("token", jwt);
		response.put("type", "Bearer");
		response.put("user", Map.of(
				"id", user.getId(),
				"email", user.getEmail(),
				"nom", user.getNom(),
				"role", user.getRole().name()
		));

		return ResponseEntity.ok(response);
	}

	@PostMapping("/register")
	@Operation(summary = "Inscription", description = "Crée un nouveau compte utilisateur")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Inscription réussie",
					content = @Content(mediaType = "application/json",
							examples = @ExampleObject(value = "{\n" +
									"  \"token\": \"eyJhbGciOiJIUzUxMiJ9...\",\n" +
									"  \"type\": \"Bearer\",\n" +
									"  \"user\": {\n" +
									"    \"id\": 1,\n" +
									"    \"email\": \"jean.dupont@test.com\",\n" +
									"    \"nom\": \"Jean Dupont\",\n" +
									"    \"role\": \"CLIENT\"\n" +
									"  }\n" +
									"}"))),
			@ApiResponse(responseCode = "400", description = "Email déjà utilisé ou données invalides")
	})
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "Données d'inscription",
			required = true,
			content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = Map.class),
					examples = @ExampleObject(
							name = "Exemple d'inscription CLIENT",
							value = "{\n" +
									"  \"nom\": \"Jean Dupont\",\n" +
									"  \"email\": \"jean.dupont@test.com\",\n" +
									"  \"password\": \"password123\",\n" +
									"  \"role\": \"CLIENT\"\n" +
									"}"
					)
			)
	)
	public ResponseEntity<?> register(@RequestBody Map<String, String> registerRequest) {
		String email = registerRequest.get("email");
		String password = registerRequest.get("password");
		String nom = registerRequest.get("nom");
		String roleStr = registerRequest.getOrDefault("role", "CLIENT");

		if (email == null || email.isEmpty()) {
			throw new ApiException("L'email est requis", HttpStatus.BAD_REQUEST);
		}
		if (password == null || password.isEmpty()) {
			throw new ApiException("Le mot de passe est requis", HttpStatus.BAD_REQUEST);
		}
		if (nom == null || nom.isEmpty()) {
			throw new ApiException("Le nom est requis", HttpStatus.BAD_REQUEST);
		}

		if (userRepository.findByEmail(email).isPresent()) {
			throw new ApiException("Cet email est déjà utilisé", HttpStatus.BAD_REQUEST);
		}

		Role role;
		try {
			role = Role.valueOf(roleStr.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new ApiException("Rôle invalide. Valeurs acceptées: CLIENT, AGENT, ADMIN", HttpStatus.BAD_REQUEST);
		}

		User user = new User();
		user.setEmail(email);
		user.setPassword(passwordEncoder.encode(password));
		user.setNom(nom);
		user.setRole(role);

		user = userRepository.save(user);

		String jwt = jwtUtils.generateJwtToken(email);

		Map<String, Object> response = new HashMap<>();
		response.put("token", jwt);
		response.put("type", "Bearer");
		response.put("user", Map.of(
				"id", user.getId(),
				"email", user.getEmail(),
				"nom", user.getNom(),
				"role", user.getRole().name()
		));

		return ResponseEntity.ok(response);
	}
}

