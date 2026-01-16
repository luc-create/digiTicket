package com.gigiTicket.backend.controller;

import com.gigiTicket.backend.model.Notification;
import com.gigiTicket.backend.model.User;
import com.gigiTicket.backend.repository.UserRepository;
import com.gigiTicket.backend.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Gestion des notifications")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

	private final NotificationService notificationService;
	private final UserRepository userRepository;

	@GetMapping
	@Operation(summary = "Récupérer mes notifications", description = "Retourne toutes les notifications de l'utilisateur connecté")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Liste des notifications"),
			@ApiResponse(responseCode = "401", description = "Non authentifié")
	})
	public ResponseEntity<List<Notification>> getMyNotifications() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User currentUser = userRepository.findByEmail(auth.getName())
				.orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

		return ResponseEntity.ok(notificationService.getUserNotifications(currentUser.getId()));
	}

	@GetMapping("/unread")
	@Operation(summary = "Récupérer mes notifications non lues", description = "Retourne les notifications non lues de l'utilisateur connecté")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Liste des notifications non lues"),
			@ApiResponse(responseCode = "401", description = "Non authentifié")
	})
	public ResponseEntity<List<Notification>> getMyUnreadNotifications() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User currentUser = userRepository.findByEmail(auth.getName())
				.orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

		return ResponseEntity.ok(notificationService.getUserUnreadNotifications(currentUser.getId()));
	}

	@PutMapping("/{notificationId}/read")
	@Operation(summary = "Marquer une notification comme lue", description = "Marque une notification spécifique comme lue")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Notification marquée comme lue"),
			@ApiResponse(responseCode = "401", description = "Non authentifié"),
			@ApiResponse(responseCode = "403", description = "Accès refusé - notification ne vous appartient pas"),
			@ApiResponse(responseCode = "404", description = "Notification non trouvée")
	})
	@Parameter(name = "notificationId", description = "ID de la notification", example = "1", required = true)
	public ResponseEntity<Notification> markAsRead(@PathVariable Integer notificationId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User currentUser = userRepository.findByEmail(auth.getName())
				.orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

		return ResponseEntity.ok(notificationService.markAsRead(notificationId, currentUser.getId()));
	}

	@PutMapping("/read-all")
	@Operation(summary = "Marquer toutes les notifications comme lues", description = "Marque toutes les notifications de l'utilisateur comme lues")
	public ResponseEntity<Void> markAllAsRead() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User currentUser = userRepository.findByEmail(auth.getName())
				.orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

		notificationService.markAllAsRead(currentUser.getId());
		return ResponseEntity.ok().build();
	}
}

