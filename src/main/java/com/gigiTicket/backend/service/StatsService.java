package com.gigiTicket.backend.service;

import com.gigiTicket.backend.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatsService {

	private final TicketRepository ticketRepository;

	public Map<String, Integer> getTicketsByStatus() {
		List<Object[]> results = ticketRepository.countByStatus();
		Map<String, Integer> stats = new HashMap<>();
		
		for (Object[] result : results) {
			String status = result[0].toString();
			Long count = (Long) result[1];
			stats.put(status, count.intValue());
		}
		
		return stats;
	}

	public List<Map<String, Object>> getTicketsPerAgent() {
		List<Object[]> results = ticketRepository.countByAgent();
		
		return results.stream()
				.map(result -> {
					Map<String, Object> map = new HashMap<>();
					map.put("agentId", result[0]);
					map.put("agentName", result[1]);
					map.put("totalTickets", ((Long) result[2]).intValue());
					return map;
				})
				.toList();
	}

	public List<Map<String, Object>> getTicketsPerClient() {
		List<Object[]> results = ticketRepository.countByClient();
		
		return results.stream()
				.map(result -> {
					Map<String, Object> map = new HashMap<>();
					map.put("clientId", result[0]);
					map.put("clientName", result[1]);
					map.put("totalTickets", ((Long) result[2]).intValue());
					return map;
				})
				.toList();
	}
}

