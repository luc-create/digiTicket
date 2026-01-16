package com.gigiTicket.backend.service;

import com.gigiTicket.backend.exception.ApiException;
import com.gigiTicket.backend.model.User;
import com.gigiTicket.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UtilisateurService {

    private final UserRepository repository;

    public UtilisateurService(UserRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public User creer(User utilisateur) {
        if (repository.existsByEmail(utilisateur.getEmail())) {
            throw new ApiException("Email déjà utilisé", HttpStatus.BAD_REQUEST);
        }
        utilisateur.setActive(false);
        return repository.save(utilisateur);
    }

    public List<User> lister() {
        return repository.findAll();
    }

    public User trouverParId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ApiException("Utilisateur non trouvé avec l'id: " + id, HttpStatus.NOT_FOUND));
    }

    @Transactional
    public User modifier(Integer id, User utilisateur) {
        User u = trouverParId(id);

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

        return repository.save(u);
    }

    @Transactional
    public void supprimer(Integer id) {
        if (!repository.existsById(id)) {
            throw new ApiException("Utilisateur non trouvé avec l'id: " + id, HttpStatus.NOT_FOUND);
        }
        repository.deleteById(id);
    }

    public List<User> listerActifs() {
        return repository.findByActiveTrue();
    }

    @Transactional
    public User activer(Integer id) {
        User utilisateur = trouverParId(id);
        utilisateur.setActive(true);
        return repository.save(utilisateur);
    }

    @Transactional
    public User desactiver(Integer id) {
        User utilisateur = trouverParId(id);
        utilisateur.setActive(false);
        return repository.save(utilisateur);
    }
}
