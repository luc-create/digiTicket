package com.gigiTicket.backend.service;

import com.gigiTicket.backend.entity.Utilisateur;
import com.gigiTicket.backend.exception.ApiException;
import com.gigiTicket.backend.repository.UtilisateurRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UtilisateurService {

    private final UtilisateurRepository repository;

    public UtilisateurService(UtilisateurRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Utilisateur creer(Utilisateur utilisateur) {
        if (repository.existsByEmail(utilisateur.getEmail())) {
            throw new ApiException("Email déjà utilisé", HttpStatus.BAD_REQUEST);
        }
        utilisateur.setActive(false);
        return repository.save(utilisateur);
    }

    public List<Utilisateur> lister() {
        return repository.findAll();
    }

    public List<Utilisateur> listerActifs() {
        return repository.findByActiveTrue();
    }

    public Utilisateur trouverParId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ApiException("Utilisateur non trouvé avec l'id: " + id, HttpStatus.NOT_FOUND));
    }

    @Transactional
    public Utilisateur modifier(Integer id, Utilisateur utilisateur) {
        Utilisateur u = trouverParId(id);

        if (utilisateur.getEmail() != null && !utilisateur.getEmail().equals(u.getEmail())) {
            if (repository.existsByEmail(utilisateur.getEmail())) {
                throw new ApiException("Cet email est déjà utilisé", HttpStatus.BAD_REQUEST);
            }
        }

        if (utilisateur.getNom() != null) {
            u.setNom(utilisateur.getNom());
        }
        if (utilisateur.getEmail() != null) {
            u.setEmail(utilisateur.getEmail());
        }
        if (utilisateur.getTelephone() != null) {
            u.setTelephone(utilisateur.getTelephone());
        }
        if (utilisateur.getRole() != null) {
            u.setRole(utilisateur.getRole());
        }
        // Note: Le mot de passe doit être encodé avant d'être sauvegardé
        // Cette modification devrait être gérée séparément avec un endpoint dédié

        return repository.save(u);
    }

    @Transactional
    public void supprimer(Integer id) {
        if (!repository.existsById(id)) {
            throw new ApiException("Utilisateur non trouvé avec l'id: " + id, HttpStatus.NOT_FOUND);
        }
        repository.deleteById(id);
    }

    @Transactional
    public Utilisateur activer(Integer id) {
        Utilisateur utilisateur = trouverParId(id);
        utilisateur.setActive(true);
        return repository.save(utilisateur);
    }

    @Transactional
    public Utilisateur desactiver(Integer id) {
        Utilisateur utilisateur = trouverParId(id);
        utilisateur.setActive(false);
        return repository.save(utilisateur);
    }
}
