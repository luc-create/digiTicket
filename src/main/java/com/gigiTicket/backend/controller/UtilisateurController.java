package com.gigiTicket.backend.controller;

import com.gigiTicket.backend.entity.Utilisateur;
import com.gigiTicket.backend.service.UtilisateurService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
@CrossOrigin
public class UtilisateurController {

	private final UtilisateurService service;

	public UtilisateurController(UtilisateurService service) {
		this.service = service;
	}

	// Créer un utilisateur (compte désactivé par défaut)
	@PostMapping
	public Utilisateur creer(@RequestBody Utilisateur utilisateur) {
		return service.creer(utilisateur);
	}

	// Lister tous les utilisateurs
	@GetMapping
	public List<Utilisateur> lister() {
		return service.lister();
	}

	// Lister uniquement les utilisateurs actifs
	@GetMapping("/actifs")
	public List<Utilisateur> listerActifs() {
		return service.listerActifs();
	}

	// Récupérer un utilisateur par ID
	@GetMapping("/{id}")
	public Utilisateur getById(@PathVariable Long id) {
		return service.trouverParId(id);
	}

	// Modifier un utilisateur
	@PutMapping("/{id}")
	public Utilisateur modifier(@PathVariable Long id, @RequestBody Utilisateur utilisateur) {
		return service.modifier(id, utilisateur);
	}

	// Supprimer un utilisateur
	@DeleteMapping("/{id}")
	public void supprimer(@PathVariable Long id) {
		service.supprimer(id);
	}

	// Activer un compte utilisateur
	@PatchMapping("/{id}/activer")
	public Utilisateur activer(@PathVariable Long id) {
		return service.activer(id);
	}

	// Désactiver un compte utilisateur
	@PatchMapping("/{id}/desactiver")
	public Utilisateur desactiver(@PathVariable Long id) {
		return service.desactiver(id);
	}
}
