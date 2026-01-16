package com.gigiTicket.backend.service;

import com.gigiTicket.backend.model.Notification;
import com.gigiTicket.backend.model.Ticket;
import com.gigiTicket.backend.model.User;
import com.gigiTicket.backend.repository.NotificationRepository;
import com.gigiTicket.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

	private final NotificationRepository notificationRepository;
	private final UserRepository userRepository;

	public List<Notification> getUserNotifications(Integer userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'id: " + userId));
		return notificationRepository.findByUserOrderByDateCreationDesc(user);
	}

	public List<Notification> getUserUnreadNotifications(Integer userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'id: " + userId));
		return notificationRepository.findByUserAndLueFalseOrderByDateCreationDesc(user);
	}

	@Transactional
	public Notification markAsRead(Integer notificationId, Integer userId) {
		Notification notification = notificationRepository.findById(notificationId)
				.orElseThrow(() -> new RuntimeException("Notification non trouvée avec l'id: " + notificationId));

		if (!notification.getUser().getId().equals(userId)) {
			throw new RuntimeException("Accès refusé : cette notification ne vous appartient pas");
		}

		notification.setLue(true);
		return notificationRepository.save(notification);
	}

	@Transactional
	public void markAllAsRead(Integer userId) {
		List<Notification> notifications = getUserUnreadNotifications(userId);
		notifications.forEach(n -> n.setLue(true));
		notificationRepository.saveAll(notifications);
	}

	@Transactional
	public Notification createNotification(User user, Ticket ticket, String type, String titre, String message) {
		Notification notification = new Notification();
		notification.setUser(user);
		notification.setTicket(ticket);
		notification.setType(type);
		notification.setTitre(titre);
		notification.setMessage(message);
		notification.setLue(false);
		notification.setDateCreation(java.time.LocalDateTime.now());

		return notificationRepository.save(notification);
	}

	public void notifyTicketCreated(Ticket ticket) {
		createNotification(
				ticket.getClient(),
				ticket,
				"TICKET_CREATED",
				"Ticket créé",
				"Votre ticket \"" + ticket.getTitre() + "\" a été créé avec succès."
		);
	}

	public void notifyTicketAssigned(Ticket ticket, User agent) {
		createNotification(
				agent,
				ticket,
				"TICKET_ASSIGNED",
				"Ticket assigné",
				"Le ticket \"" + ticket.getTitre() + "\" vous a été assigné."
		);

		createNotification(
				ticket.getClient(),
				ticket,
				"TICKET_ASSIGNED",
				"Agent assigné",
				"Un agent a été assigné à votre ticket \"" + ticket.getTitre() + "\"."
		);
	}

	public void notifyTicketStatusChanged(Ticket ticket, String oldStatus, String newStatus) {
		createNotification(
				ticket.getClient(),
				ticket,
				"TICKET_STATUS_CHANGED",
				"Statut modifié",
				"Le statut de votre ticket \"" + ticket.getTitre() + "\" est passé de " + oldStatus + " à " + newStatus + "."
		);

		if (ticket.getAgent() != null) {
			createNotification(
					ticket.getAgent(),
					ticket,
					"TICKET_STATUS_CHANGED",
					"Statut modifié",
					"Le statut du ticket \"" + ticket.getTitre() + "\" est passé de " + oldStatus + " à " + newStatus + "."
			);
		}
	}

	public void notifyTicketEscalated(Ticket ticket) {
		createNotification(
				ticket.getClient(),
				ticket,
				"TICKET_ESCALATED",
				"Ticket escaladé",
				"Votre ticket \"" + ticket.getTitre() + "\" a été escaladé."
		);

		if (ticket.getAgent() != null) {
			createNotification(
					ticket.getAgent(),
					ticket,
					"TICKET_ESCALATED",
					"Ticket escaladé",
					"Le ticket \"" + ticket.getTitre() + "\" a été escaladé."
			);
		}
	}

	public void notifyTicketClosed(Ticket ticket) {
		createNotification(
				ticket.getClient(),
				ticket,
				"TICKET_CLOSED",
				"Ticket fermé",
				"Votre ticket \"" + ticket.getTitre() + "\" a été fermé."
		);

		if (ticket.getAgent() != null) {
			createNotification(
					ticket.getAgent(),
					ticket,
					"TICKET_CLOSED",
					"Ticket fermé",
					"Le ticket \"" + ticket.getTitre() + "\" a été fermé."
			);
		}
	}
}

