package com.gigiTicket.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ticket_id", nullable = true)
	private Ticket ticket;

	@Column(nullable = false)
	private String titre;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String message;

	@Column(nullable = false)
	private Boolean lue = false;

	@Column(nullable = false)
	private LocalDateTime dateCreation = LocalDateTime.now();

	@Column(nullable = false)
	private String type;
}

