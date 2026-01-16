package com.gigiTicket.backend.controller;

import com.gigiTicket.backend.model.User;
import com.gigiTicket.backend.service.UtilisateurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
@RequiredArgsConstructor
@Tag(name = "Utilisateurs", description = "Gestion des utilisateurs (CRUD complet)")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class UtilisateurController {

	private final UtilisateurService service;

	@PostMapping
	@Operation(summary = "Créer un utilisateur", description = "Crée un nouveau compte utilisateur (désactivé par défaut)")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Utilisateur créé avec succès"),
			@ApiResponse(responseCode = "400", description = "Email déjà utilisé ou données invalides"),
			@ApiResponse(responseCode = "401", description = "Non authentifié"),
			@ApiResponse(responseCode = "403", description = "Accès refusé - rôle ADMIN requis")
	})
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "Données de l'utilisateur à créer",
			required = true,
			content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = User.class),
					examples = @ExampleObject(
							name = "Exemple de création d'utilisateur",
							value = "{\n" +
									"  \"nom\": \"Jean Dupont\",\n" +
									"  \"email\": \"jean.dupont@test.com\",\n" +
									"  \"password\": \"password123\",\n" +
									"  \"role\": \"CLIENT\",\n" +
									"  \"telephone\": \"0123456789\"\n" +
									"}"
					)
			)
	)
	public ResponseEntity<User> creer(@RequestBody User utilisateur) {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.creer(utilisateur));
	}

	@GetMapping
	@Operation(summary = "Lister tous les utilisateurs", description = "Retourne la liste de tous les utilisateurs")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Liste des utilisateurs"),
			@ApiResponse(responseCode = "401", description = "Non authentifié"),
			@ApiResponse(responseCode = "403", description = "Accès refusé - rôle ADMIN requis")
	})
	public ResponseEntity<List<User>> lister() {
		return ResponseEntity.ok(service.lister());
	}

	@GetMapping("/actifs")
	@Operation(summary = "Lister les utilisateurs actifs", description = "Retourne uniquement les utilisateurs avec compte actif")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Liste des utilisateurs actifs"),
			@ApiResponse(responseCode = "401", description = "Non authentifié"),
			@ApiResponse(responseCode = "403", description = "Accès refusé - rôle ADMIN requis")
	})
	public ResponseEntity<List<User>> listerActifs() {
		return ResponseEntity.ok(service.listerActifs());
	}

	@GetMapping("/{id}")
	@Operation(summary = "Récupérer un utilisateur par ID", description = "Retourne un utilisateur spécifique")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Utilisateur trouvé"),
			@ApiResponse(responseCode = "401", description = "Non authentifié"),
			@ApiResponse(responseCode = "403", description = "Accès refusé - rôle ADMIN requis"),
			@ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
	})
	@Parameter(name = "id", description = "ID de l'utilisateur", example = "1", required = true)
	public ResponseEntity<User> getById(@PathVariable Integer id) {
		return ResponseEntity.ok(service.trouverParId(id));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Modifier un utilisateur", description = "Met à jour les informations d'un utilisateur")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Utilisateur modifié avec succès"),
			@ApiResponse(responseCode = "400", description = "Email déjà utilisé ou données invalides"),
			@ApiResponse(responseCode = "401", description = "Non authentifié"),
			@ApiResponse(responseCode = "403", description = "Accès refusé - rôle ADMIN requis"),
			@ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
	})
	@Parameter(name = "id", description = "ID de l'utilisateur", example = "1", required = true)
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "Données à modifier",
			required = true,
			content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = User.class),
					examples = @ExampleObject(
							name = "Exemple de modification",
							value = "{\n" +
									"  \"nom\": \"Jean Dupont Modifié\",\n" +
									"  \"telephone\": \"0987654321\",\n" +
									"  \"role\": \"AGENT\"\n" +
									"}"
					)
			)
	)
	public ResponseEntity<User> modifier(@PathVariable Integer id, @RequestBody User utilisateur) {
		return ResponseEntity.ok(service.modifier(id, utilisateur));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Supprimer un utilisateur", description = "Supprime définitivement un utilisateur")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "Utilisateur supprimé avec succès"),
			@ApiResponse(responseCode = "401", description = "Non authentifié"),
			@ApiResponse(responseCode = "403", description = "Accès refusé - rôle ADMIN requis"),
			@ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
	})
	@Parameter(name = "id", description = "ID de l'utilisateur", example = "1", required = true)
	public ResponseEntity<Void> supprimer(@PathVariable Integer id) {
		service.supprimer(id);
		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/{id}/activer")
	@Operation(summary = "Activer un compte utilisateur", description = "Active le compte d'un utilisateur")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Compte activé avec succès"),
			@ApiResponse(responseCode = "401", description = "Non authentifié"),
			@ApiResponse(responseCode = "403", description = "Accès refusé - rôle ADMIN requis"),
			@ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
	})
	@Parameter(name = "id", description = "ID de l'utilisateur", example = "1", required = true)
	public ResponseEntity<User> activer(@PathVariable Integer id) {
		return ResponseEntity.ok(service.activer(id));
	}

	@PatchMapping("/{id}/desactiver")
	@Operation(summary = "Désactiver un compte utilisateur", description = "Désactive le compte d'un utilisateur")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Compte désactivé avec succès"),
			@ApiResponse(responseCode = "401", description = "Non authentifié"),
			@ApiResponse(responseCode = "403", description = "Accès refusé - rôle ADMIN requis"),
			@ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
	})
	@Parameter(name = "id", description = "ID de l'utilisateur", example = "1", required = true)
	public ResponseEntity<User> desactiver(@PathVariable Integer id) {
		return ResponseEntity.ok(service.desactiver(id));
	}
}
