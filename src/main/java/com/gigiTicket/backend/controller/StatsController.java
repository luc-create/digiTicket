package com.gigiTicket.backend.controller;

import com.gigiTicket.backend.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
@Tag(name = "Statistiques", description = "Statistiques et dashboard")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class StatsController {

	private final StatsService statsService;

	@GetMapping("/tickets-by-status")
	@Operation(summary = "Nombre de tickets par statut", description = "Retourne le nombre de tickets pour chaque statut")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Statistiques récupérées avec succès"),
			@ApiResponse(responseCode = "401", description = "Non authentifié"),
			@ApiResponse(responseCode = "403", description = "Accès refusé - rôle ADMIN requis")
	})
	public ResponseEntity<Map<String, Integer>> getTicketsByStatus() {
		return ResponseEntity.ok(statsService.getTicketsByStatus());
	}

	@GetMapping("/tickets-per-agent")
	@Operation(summary = "Nombre de tickets par agent", description = "Retourne le nombre de tickets assignés à chaque agent")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Statistiques récupérées avec succès"),
			@ApiResponse(responseCode = "401", description = "Non authentifié"),
			@ApiResponse(responseCode = "403", description = "Accès refusé - rôle ADMIN requis")
	})
	public ResponseEntity<List<Map<String, Object>>> getTicketsPerAgent() {
		return ResponseEntity.ok(statsService.getTicketsPerAgent());
	}

	@GetMapping("/tickets-per-client")
	@Operation(summary = "Nombre de tickets par client", description = "Retourne le nombre de tickets créés par chaque client")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Statistiques récupérées avec succès"),
			@ApiResponse(responseCode = "401", description = "Non authentifié"),
			@ApiResponse(responseCode = "403", description = "Accès refusé - rôle ADMIN requis")
	})
	public ResponseEntity<List<Map<String, Object>>> getTicketsPerClient() {
		return ResponseEntity.ok(statsService.getTicketsPerClient());
	}
}

