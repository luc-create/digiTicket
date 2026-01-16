package com.gigiTicket.backend.controller;

import com.gigiTicket.backend.model.AdminLog;
import com.gigiTicket.backend.model.Role;
import com.gigiTicket.backend.model.Ticket;
import com.gigiTicket.backend.model.User;
import com.gigiTicket.backend.repository.UserRepository;
import com.gigiTicket.backend.service.AdminService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Administration", description = "Endpoints d'administration (ADMIN uniquement)")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

	private final AdminService adminService;
	private final UserRepository userRepository;

	@GetMapping("/users")
	@Operation(summary = "Liste tous les utilisateurs", description = "Retourne la liste de tous les utilisateurs")
	public ResponseEntity<List<User>> getAllUsers() {
		return ResponseEntity.ok(adminService.getAllUsers());
	}

	@GetMapping("/users/{id}")
	@Operation(summary = "Récupérer un utilisateur", description = "Retourne un utilisateur par son id")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Utilisateur trouvé"),
			@ApiResponse(responseCode = "401", description = "Non authentifié"),
			@ApiResponse(responseCode = "403", description = "Accès refusé - rôle ADMIN requis"),
			@ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
	})
	@Parameter(name = "id", description = "ID de l'utilisateur", example = "1", required = true)
	public ResponseEntity<User> getUserById(@PathVariable Integer id) {
		return ResponseEntity.ok(adminService.getUserById(id));
	}

	@PutMapping("/users/{userId}/role")
	@Operation(summary = "Modifier le rôle d'un utilisateur", description = "Change le rôle d'un utilisateur")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Rôle modifié avec succès"),
			@ApiResponse(responseCode = "400", description = "Rôle invalide"),
			@ApiResponse(responseCode = "401", description = "Non authentifié"),
			@ApiResponse(responseCode = "403", description = "Accès refusé - rôle ADMIN requis"),
			@ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
	})
	@Parameter(name = "userId", description = "ID de l'utilisateur", example = "1", required = true)
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "Nouveau rôle de l'utilisateur",
			required = true,
			content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = Map.class),
					examples = {
							@ExampleObject(
									name = "Rôle CLIENT",
									value = "{\n  \"role\": \"CLIENT\"\n}"
							),
							@ExampleObject(
									name = "Rôle AGENT",
									value = "{\n  \"role\": \"AGENT\"\n}"
							),
							@ExampleObject(
									name = "Rôle ADMIN",
									value = "{\n  \"role\": \"ADMIN\"\n}"
							)
					}
			)
	)
	public ResponseEntity<User> updateUserRole(
			@PathVariable Integer userId,
			@RequestBody Map<String, String> request) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User admin = userRepository.findByEmail(auth.getName())
				.orElseThrow(() -> new RuntimeException("Admin non trouvé"));

		Role newRole = Role.valueOf(request.get("role").toUpperCase());
		return ResponseEntity.ok(adminService.updateUserRole(userId, newRole, admin.getId()));
	}

	@PutMapping("/users/{userId}/activate")
	@Operation(summary = "Activer un compte utilisateur", description = "Active le compte d'un utilisateur")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Compte activé"),
			@ApiResponse(responseCode = "401", description = "Non authentifié"),
			@ApiResponse(responseCode = "403", description = "Accès refusé - rôle ADMIN requis"),
			@ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
	})
	@Parameter(name = "userId", description = "ID de l'utilisateur", example = "1", required = true)
	public ResponseEntity<User> activateUser(@PathVariable Integer userId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User admin = userRepository.findByEmail(auth.getName())
				.orElseThrow(() -> new RuntimeException("Admin non trouvé"));

		return ResponseEntity.ok(adminService.activateUser(userId, admin.getId()));
	}

	@PutMapping("/users/{userId}/deactivate")
	@Operation(summary = "Désactiver un compte utilisateur", description = "Désactive le compte d'un utilisateur")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Compte désactivé"),
			@ApiResponse(responseCode = "401", description = "Non authentifié"),
			@ApiResponse(responseCode = "403", description = "Accès refusé - rôle ADMIN requis"),
			@ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
	})
	@Parameter(name = "userId", description = "ID de l'utilisateur", example = "1", required = true)
	public ResponseEntity<User> deactivateUser(@PathVariable Integer userId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User admin = userRepository.findByEmail(auth.getName())
				.orElseThrow(() -> new RuntimeException("Admin non trouvé"));

		return ResponseEntity.ok(adminService.deactivateUser(userId, admin.getId()));
	}

	@PutMapping("/tickets/{ticketId}/assign/{agentId}")
	@Operation(summary = "Assigner un ticket à un agent (Admin)", description = "Assignation forcée d'un ticket par un administrateur")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Ticket assigné avec succès"),
			@ApiResponse(responseCode = "400", description = "Erreur de validation"),
			@ApiResponse(responseCode = "401", description = "Non authentifié"),
			@ApiResponse(responseCode = "403", description = "Accès refusé - rôle ADMIN requis"),
			@ApiResponse(responseCode = "404", description = "Ticket ou agent non trouvé")
	})
	@Parameter(name = "ticketId", description = "ID du ticket", example = "1", required = true)
	@Parameter(name = "agentId", description = "ID de l'agent", example = "2", required = true)
	public ResponseEntity<Ticket> assignTicketByAdmin(
			@PathVariable Integer ticketId,
			@PathVariable Integer agentId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User admin = userRepository.findByEmail(auth.getName())
				.orElseThrow(() -> new RuntimeException("Admin non trouvé"));

		return ResponseEntity.ok(adminService.assignTicketByAdmin(ticketId, agentId, admin.getId()));
	}

	@GetMapping("/logs")
	@Operation(summary = "Récupérer les logs d'administration", description = "Retourne tous les logs d'actions administratives")
	public ResponseEntity<List<AdminLog>> getAllAdminLogs() {
		return ResponseEntity.ok(adminService.getAllAdminLogs());
	}

	@GetMapping("/logs/my")
	@Operation(summary = "Mes logs d'administration", description = "Retourne les logs de l'administrateur connecté")
	public ResponseEntity<List<AdminLog>> getMyAdminLogs() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User admin = userRepository.findByEmail(auth.getName())
				.orElseThrow(() -> new RuntimeException("Admin non trouvé"));

		return ResponseEntity.ok(adminService.getAdminLogs(admin.getId()));
	}
}

