package com.gigiTicket.backend.service;

import com.gigiTicket.backend.model.Ticket;
import com.gigiTicket.backend.model.TicketStatus;
import com.gigiTicket.backend.model.User;
import com.gigiTicket.backend.repository.TicketRepository;
import com.gigiTicket.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

	private final TicketRepository ticketRepository;
	private final UserRepository userRepository;
	private final NotificationService notificationService;

	public List<Ticket> getAllTickets() {
		return ticketRepository.findAll();
	}

	public Ticket getTicketById(Integer id) {
		return ticketRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Ticket non trouvé avec l'id: " + id));
	}

	public List<Ticket> getTicketsByClient(Integer clientId) {
		User client = userRepository.findById(clientId)
				.orElseThrow(() -> new RuntimeException("Client non trouvé avec l'id: " + clientId));
		return ticketRepository.findByClient(client);
	}

	public List<Ticket> getTicketsByAgent(Integer agentId) {
		User agent = userRepository.findById(agentId)
				.orElseThrow(() -> new RuntimeException("Agent non trouvé avec l'id: " + agentId));
		return ticketRepository.findByAgent(agent);
	}

	@Transactional
	public Ticket createTicket(String titre, String description, Integer clientId) {
		User client = userRepository.findById(clientId)
				.orElseThrow(() -> new RuntimeException("Client non trouvé avec l'id: " + clientId));

		Ticket ticket = new Ticket();
		ticket.setTitre(titre);
		ticket.setDescription(description);
		ticket.setClient(client);
		ticket.setStatut(TicketStatus.OPEN);
		ticket.setDateCreation(java.time.LocalDateTime.now());

		ticket = ticketRepository.save(ticket);
		
		notificationService.notifyTicketCreated(ticket);
		
		return ticket;
	}

	@Transactional
	public Ticket assignAgent(Integer ticketId, Integer agentId) {
		Ticket ticket = ticketRepository.findById(ticketId)
				.orElseThrow(() -> new RuntimeException("Ticket non trouvé avec l'id: " + ticketId));

		User agent = userRepository.findById(agentId)
				.orElseThrow(() -> new RuntimeException("Agent non trouvé avec l'id: " + agentId));

		if (!agent.getRole().name().equals("AGENT") && !agent.getRole().name().equals("ADMIN")) {
			throw new RuntimeException("L'utilisateur doit être un agent");
		}

		if (ticket.getStatut() == TicketStatus.CLOSED) {
			throw new RuntimeException("Impossible d'assigner un agent à un ticket fermé");
		}

		ticket.setAgent(agent);
		if (ticket.getStatut() == TicketStatus.OPEN) {
			ticket.setStatut(TicketStatus.IN_PROGRESS);
		}
		ticket = ticketRepository.save(ticket);
		
		notificationService.notifyTicketAssigned(ticket, agent);
		
		return ticket;
	}

	@Transactional
	public Ticket updateStatus(Integer ticketId, TicketStatus statut) {
		Ticket ticket = ticketRepository.findById(ticketId)
				.orElseThrow(() -> new RuntimeException("Ticket non trouvé avec l'id: " + ticketId));

		if (ticket.getStatut() == TicketStatus.CLOSED && statut != TicketStatus.CLOSED) {
			throw new RuntimeException("Impossible de modifier le statut d'un ticket fermé");
		}

		if (statut == TicketStatus.IN_PROGRESS && ticket.getAgent() == null) {
			throw new RuntimeException("Un agent doit être assigné avant de passer en IN_PROGRESS");
		}

		String oldStatus = ticket.getStatut().name();
		ticket.setStatut(statut);
		ticket = ticketRepository.save(ticket);
		
		notificationService.notifyTicketStatusChanged(ticket, oldStatus, statut.name());
		
		return ticket;
	}

	@Transactional
	public Ticket closeTicket(Integer ticketId) {
		Ticket ticket = ticketRepository.findById(ticketId)
				.orElseThrow(() -> new RuntimeException("Ticket non trouvé avec l'id: " + ticketId));

		if (ticket.getStatut() == TicketStatus.CLOSED) {
			throw new RuntimeException("Le ticket est déjà fermé");
		}

		ticket.setStatut(TicketStatus.CLOSED);
		ticket = ticketRepository.save(ticket);
		
		notificationService.notifyTicketClosed(ticket);
		
		return ticket;
	}

	@Transactional
	public Ticket escalateTicket(Integer ticketId) {
		Ticket ticket = ticketRepository.findById(ticketId)
				.orElseThrow(() -> new RuntimeException("Ticket non trouvé avec l'id: " + ticketId));

		if (ticket.getStatut() == TicketStatus.CLOSED) {
			throw new RuntimeException("Impossible d'escalader un ticket fermé");
		}

		if (ticket.getStatut() == TicketStatus.ESCALATED) {
			throw new RuntimeException("Le ticket est déjà escaladé");
		}

		ticket.setStatut(TicketStatus.ESCALATED);
		ticket = ticketRepository.save(ticket);
		
		notificationService.notifyTicketEscalated(ticket);
		
		return ticket;
	}
}

