package com.gigiTicket.backend.repository;

import com.gigiTicket.backend.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {
    boolean existsByEmail(String email);
    Optional<Utilisateur> findByEmail(String email);
    List<Utilisateur> findByActiveTrue();
}
