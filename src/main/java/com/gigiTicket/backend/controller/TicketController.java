package com.gigiTicket.backend.controller;

import com.gigiTicket.backend.model.Ticket;
import com.gigiTicket.backend.model.TicketStatus;
import com.gigiTicket.backend.model.User;
import com.gigiTicket.backend.repository.UserRepository;
import com.gigiTicket.backend.service.TicketService;
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
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@Tag(name = "Tickets", description = "Gestion des tickets")
@SecurityRequirement(name = "bearerAuth")
public class TicketController {

	private final TicketService ticketService;
	private final UserRepository userRepository;

	@PostMapping
	@Operation(summary = "Créer un ticket", description = "Crée un nouveau ticket pour le client authentifié")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Ticket créé avec succès"),
			@ApiResponse(responseCode = "401", description = "Non authentifié"),
			@ApiResponse(responseCode = "403", description = "Accès refusé - rôle CLIENT requis")
	})
	@PreAuthorize("hasRole('CLIENT')")
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "Données du ticket à créer",
			required = true,
			content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = Map.class),
					examples = @ExampleObject(
							name = "Exemple de création de ticket",
							value = "{\n" +
									"  \"titre\": \"Imprimante cassée\",\n" +
									"  \"description\": \"L'imprimante ne fonctionne plus depuis ce matin. Elle affiche une erreur et ne répond plus.\"\n" +
									"}"
					)
			)
	)
	public ResponseEntity<Ticket> createTicket(@RequestBody Map<String, Object> request) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User currentUser = userRepository.findByEmail(auth.getName())
				.orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

		String titre = (String) request.get("titre");
		String description = (String) request.get("description");

		Ticket ticket = ticketService.createTicket(titre, description, currentUser.getId());
		return ResponseEntity.ok(ticket);
	}

	@GetMapping
	@Operation(summary = "Récupérer tous les tickets", description = "Retourne la liste de tous les tickets (ADMIN/AGENT)")
	@PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
	public ResponseEntity<List<Ticket>> getAllTickets() {
		return ResponseEntity.ok(ticketService.getAllTickets());
	}

	@GetMapping("/{id}")
	@Operation(summary = "Récupérer un ticket par son id", description = "Retourne un ticket spécifique")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Ticket trouvé"),
			@ApiResponse(responseCode = "401", description = "Non authentifié"),
			@ApiResponse(responseCode = "403", description = "Accès refusé"),
			@ApiResponse(responseCode = "404", description = "Ticket non trouvé")
	})
	@Parameter(name = "id", description = "ID du ticket", example = "1", required = true)
	public ResponseEntity<Ticket> getTicketById(@PathVariable Integer id) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User currentUser = userRepository.findByEmail(auth.getName())
				.orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

		Ticket ticket = ticketService.getTicketById(id);

		if (currentUser.getRole().name().equals("CLIENT") && !ticket.getClient().getId().equals(currentUser.getId())) {
			throw new RuntimeException("Accès refusé : ce ticket ne vous appartient pas");
		}

		if (currentUser.getRole().name().equals("AGENT") && 
			(ticket.getAgent() == null || !ticket.getAgent().getId().equals(currentUser.getId()))) {
			if (!currentUser.getRole().name().equals("ADMIN")) {
				throw new RuntimeException("Accès refusé : ce ticket ne vous est pas assigné");
			}
		}

		return ResponseEntity.ok(ticket);
	}

	@GetMapping("/client/{clientId}")
	@Operation(summary = "Récupérer les tickets d'un client", description = "Retourne tous les tickets d'un client spécifique")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Liste des tickets"),
			@ApiResponse(responseCode = "401", description = "Non authentifié"),
			@ApiResponse(responseCode = "403", description = "Accès refusé")
	})
	@PreAuthorize("hasRole('CLIENT')")
	@Parameter(name = "clientId", description = "ID du client", example = "1", required = true)
	public ResponseEntity<List<Ticket>> getTicketsByClient(@PathVariable Integer clientId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User currentUser = userRepository.findByEmail(auth.getName())
				.orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

		if (!currentUser.getId().equals(clientId) && !currentUser.getRole().name().equals("ADMIN")) {
			throw new RuntimeException("Accès refusé : vous ne pouvez voir que vos propres tickets");
		}

		return ResponseEntity.ok(ticketService.getTicketsByClient(clientId));
	}

	@GetMapping("/agent/{agentId}")
	@Operation(summary = "Récupérer les tickets d'un agent", description = "Retourne tous les tickets assignés à un agent")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Liste des tickets"),
			@ApiResponse(responseCode = "401", description = "Non authentifié"),
			@ApiResponse(responseCode = "403", description = "Accès refusé")
	})
	@PreAuthorize("hasRole('AGENT')")
	@Parameter(name = "agentId", description = "ID de l'agent", example = "2", required = true)
	public ResponseEntity<List<Ticket>> getTicketsByAgent(@PathVariable Integer agentId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User currentUser = userRepository.findByEmail(auth.getName())
				.orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

		if (!currentUser.getId().equals(agentId) && !currentUser.getRole().name().equals("ADMIN")) {
			throw new RuntimeException("Accès refusé : vous ne pouvez voir que vos propres tickets assignés");
		}

		return ResponseEntity.ok(ticketService.getTicketsByAgent(agentId));
	}

	@PutMapping("/{ticketId}/assign/{agentId}")
	@Operation(summary = "Assigner un agent à un ticket", description = "Assigne un agent à un ticket spécifique")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Agent assigné avec succès"),
			@ApiResponse(responseCode = "400", description = "Erreur de validation"),
			@ApiResponse(responseCode = "401", description = "Non authentifié"),
			@ApiResponse(responseCode = "403", description = "Accès refusé"),
			@ApiResponse(responseCode = "404", description = "Ticket ou agent non trouvé")
	})
	@PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
	@Parameter(name = "ticketId", description = "ID du ticket", example = "1", required = true)
	@Parameter(name = "agentId", description = "ID de l'agent", example = "2", required = true)
	public ResponseEntity<Ticket> assignAgent(@PathVariable Integer ticketId, @PathVariable Integer agentId) {
		return ResponseEntity.ok(ticketService.assignAgent(ticketId, agentId));
	}

	@PutMapping("/{ticketId}/status")
	@Operation(summary = "Modifier le statut d'un ticket", description = "Met à jour le statut d'un ticket")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Statut modifié avec succès"),
			@ApiResponse(responseCode = "400", description = "Statut invalide ou erreur de validation"),
			@ApiResponse(responseCode = "401", description = "Non authentifié"),
			@ApiResponse(responseCode = "403", description = "Accès refusé"),
			@ApiResponse(responseCode = "404", description = "Ticket non trouvé")
	})
	@PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
	@Parameter(name = "ticketId", description = "ID du ticket", example = "1", required = true)
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "Nouveau statut du ticket",
			required = true,
			content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = Map.class),
					examples = {
							@ExampleObject(
									name = "Statut IN_PROGRESS",
									value = "{\n  \"statut\": \"IN_PROGRESS\"\n}"
							),
							@ExampleObject(
									name = "Statut ESCALATED",
									value = "{\n  \"statut\": \"ESCALATED\"\n}"
							),
							@ExampleObject(
									name = "Statut CLOSED",
									value = "{\n  \"statut\": \"CLOSED\"\n}"
							)
					}
			)
	)
	public ResponseEntity<Ticket> updateStatus(@PathVariable Integer ticketId, @RequestBody Map<String, String> request) {
		TicketStatus statut = TicketStatus.valueOf(request.get("statut"));
		return ResponseEntity.ok(ticketService.updateStatus(ticketId, statut));
	}

	@PutMapping("/{ticketId}/close")
	@Operation(summary = "Fermer un ticket", description = "Ferme un ticket en mettant son statut à CLOSED")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Ticket fermé avec succès"),
			@ApiResponse(responseCode = "400", description = "Ticket déjà fermé"),
			@ApiResponse(responseCode = "401", description = "Non authentifié"),
			@ApiResponse(responseCode = "403", description = "Accès refusé"),
			@ApiResponse(responseCode = "404", description = "Ticket non trouvé")
	})
	@PreAuthorize("hasAnyRole('ADMIN', 'AGENT', 'CLIENT')")
	@Parameter(name = "ticketId", description = "ID du ticket", example = "1", required = true)
	public ResponseEntity<Ticket> closeTicket(@PathVariable Integer ticketId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User currentUser = userRepository.findByEmail(auth.getName())
				.orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

		Ticket ticket = ticketService.getTicketById(ticketId);

		if (currentUser.getRole().name().equals("CLIENT") && !ticket.getClient().getId().equals(currentUser.getId())) {
			throw new RuntimeException("Accès refusé : vous ne pouvez fermer que vos propres tickets");
		}

		return ResponseEntity.ok(ticketService.closeTicket(ticketId));
	}

	@PutMapping("/{ticketId}/escalate")
	@Operation(summary = "Escalader un ticket", description = "Escalade un ticket vers le statut ESCALATED")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Ticket escaladé avec succès"),
			@ApiResponse(responseCode = "400", description = "Ticket déjà escaladé ou fermé"),
			@ApiResponse(responseCode = "401", description = "Non authentifié"),
			@ApiResponse(responseCode = "403", description = "Accès refusé"),
			@ApiResponse(responseCode = "404", description = "Ticket non trouvé")
	})
	@PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
	@Parameter(name = "ticketId", description = "ID du ticket", example = "1", required = true)
	public ResponseEntity<Ticket> escalateTicket(@PathVariable Integer ticketId) {
		return ResponseEntity.ok(ticketService.escalateTicket(ticketId));
	}
}

