package com.gigiTicket.backend.service;

import com.gigiTicket.backend.model.AdminLog;
import com.gigiTicket.backend.model.Role;
import com.gigiTicket.backend.model.Ticket;
import com.gigiTicket.backend.model.User;
import com.gigiTicket.backend.repository.AdminLogRepository;
import com.gigiTicket.backend.repository.TicketRepository;
import com.gigiTicket.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

	private final UserRepository userRepository;
	private final TicketRepository ticketRepository;
	private final AdminLogRepository adminLogRepository;
	private final NotificationService notificationService;

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	public User getUserById(Integer id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'id: " + id));
	}

	@Transactional
	public User updateUserRole(Integer userId, Role newRole, Integer adminId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'id: " + userId));

		Role oldRole = user.getRole();
		user.setRole(newRole);
		user = userRepository.save(user);

		logAdminAction(adminId, "UPDATE_USER_ROLE", 
				"Changement de rôle: " + oldRole + " -> " + newRole + " pour l'utilisateur " + user.getEmail(), 
				userId, null);

		return user;
	}

	@Transactional
	public User activateUser(Integer userId, Integer adminId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'id: " + userId));

		logAdminAction(adminId, "ACTIVATE_USER", 
				"Activation du compte utilisateur " + user.getEmail(), 
				userId, null);

		return user;
	}

	@Transactional
	public User deactivateUser(Integer userId, Integer adminId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'id: " + userId));

		logAdminAction(adminId, "DEACTIVATE_USER", 
				"Désactivation du compte utilisateur " + user.getEmail(), 
				userId, null);

		return user;
	}

	@Transactional
	public Ticket assignTicketByAdmin(Integer ticketId, Integer agentId, Integer adminId) {
		Ticket ticket = ticketRepository.findById(ticketId)
				.orElseThrow(() -> new RuntimeException("Ticket non trouvé avec l'id: " + ticketId));

		User agent = userRepository.findById(agentId)
				.orElseThrow(() -> new RuntimeException("Agent non trouvé avec l'id: " + agentId));

		if (!agent.getRole().name().equals("AGENT") && !agent.getRole().name().equals("ADMIN")) {
			throw new RuntimeException("L'utilisateur doit être un agent");
		}

		ticket.setAgent(agent);
		if (ticket.getStatut() == com.gigiTicket.backend.model.TicketStatus.OPEN) {
			ticket.setStatut(com.gigiTicket.backend.model.TicketStatus.IN_PROGRESS);
		}
		ticket = ticketRepository.save(ticket);

		logAdminAction(adminId, "ASSIGN_TICKET", 
				"Assignation du ticket #" + ticketId + " à l'agent " + agent.getEmail(), 
				agentId, ticketId);

		notificationService.notifyTicketAssigned(ticket, agent);

		return ticket;
	}

	public List<AdminLog> getAdminLogs(Integer adminId) {
		return adminLogRepository.findByAdminIdOrderByDateActionDesc(adminId);
	}

	public List<AdminLog> getAllAdminLogs() {
		return adminLogRepository.findAll();
	}

	private void logAdminAction(Integer adminId, String action, String details, Integer targetUserId, Integer targetTicketId) {
		User admin = userRepository.findById(adminId)
				.orElseThrow(() -> new RuntimeException("Admin non trouvé"));

		AdminLog log = new AdminLog();
		log.setAdmin(admin);
		log.setAction(action);
		log.setDetails(details);
		
		if (targetUserId != null) {
			log.setTargetUser(userRepository.findById(targetUserId).orElse(null));
		}
		
		if (targetTicketId != null) {
			log.setTargetTicket(ticketRepository.findById(targetTicketId).orElse(null));
		}

		adminLogRepository.save(log);
	}
}

