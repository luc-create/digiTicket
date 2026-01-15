package com.digiTicket.backend.models;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password; // Sera crypté via BCrypt [cite: 47]

    @Enumerated(EnumType.STRING)
    private Role role; // Gestion des rôles [cite: 40]
}
