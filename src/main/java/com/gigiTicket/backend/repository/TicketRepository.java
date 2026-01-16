package com.gigiTicket.backend.repository;

import com.gigiTicket.backend.model.Ticket;
import com.gigiTicket.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {

	List<Ticket> findByClient(User client);

	List<Ticket> findByAgent(User agent);

	@Query("SELECT t.statut, COUNT(t) FROM Ticket t GROUP BY t.statut")
	List<Object[]> countByStatus();

	@Query("SELECT t.agent.id, t.agent.nom, COUNT(t) FROM Ticket t WHERE t.agent IS NOT NULL GROUP BY t.agent.id, t.agent.nom")
	List<Object[]> countByAgent();

	@Query("SELECT t.client.id, t.client.nom, COUNT(t) FROM Ticket t GROUP BY t.client.id, t.client.nom")
	List<Object[]> countByClient();
}

