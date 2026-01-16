package com.gigiTicket.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "admin_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "admin_id", nullable = false)
	private User admin;

	@Column(nullable = false)
	private String action;

	@Column(columnDefinition = "TEXT")
	private String details;

	@Column(nullable = false)
	private LocalDateTime dateAction = LocalDateTime.now();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "target_user_id", nullable = true)
	private User targetUser;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "target_ticket_id", nullable = true)
	private Ticket targetTicket;
}

