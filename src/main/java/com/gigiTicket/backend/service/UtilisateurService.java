package com.gigiTicket.backend.service;

import com.gigiTicket.backend.entity.Utilisateur;
import com.gigiTicket.backend.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UtilisateurService {

    private final UtilisateurRepository repository;

    public UtilisateurService(UtilisateurRepository repository) {
        this.repository = repository;
    }

    public Utilisateur creer(Utilisateur utilisateur) {
        if (repository.existsByEmail(utilisateur.getEmail())) {
            throw new RuntimeException("Email déjà utilisé");
        }
        return repository.save(utilisateur);
    }

    public List<Utilisateur> lister() {
        return repository.findAll();
    }

    public Utilisateur trouverParId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    public Utilisateur modifier(Long id, Utilisateur utilisateur) {
        Utilisateur u = trouverParId(id);

        u.setNom(utilisateur.getNom());
        u.setEmail(utilisateur.getEmail());
        u.setTelephone(utilisateur.getTelephone());
        u.setRole(utilisateur.getRole());

        return repository.save(u);
    }

    public void supprimer(Long id) {
        repository.deleteById(id);
    }
}
