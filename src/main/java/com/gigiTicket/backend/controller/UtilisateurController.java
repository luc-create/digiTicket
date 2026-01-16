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

	@PostMapping
	public Utilisateur creer(@RequestBody Utilisateur utilisateur) {
		return service.creer(utilisateur);
	}

	@GetMapping
	public List<Utilisateur> lister() {
		return service.lister();
	}

	@GetMapping("/{id}")
	public Utilisateur getById(@PathVariable Long id) {
		return service.trouverParId(id);
	}

	@PutMapping("/{id}")
	public Utilisateur modifier(@PathVariable Long id, @RequestBody Utilisateur utilisateur) {
		return service.modifier(id, utilisateur);
	}

	@DeleteMapping("/{id}")
	public void supprimer(@PathVariable Long id) {
		service.supprimer(id);
	}
}

