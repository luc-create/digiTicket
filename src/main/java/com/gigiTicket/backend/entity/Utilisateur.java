package com.gigiTicket.backend.entity;

import jakarta.persistence.*;
import lombok.Setter;

@Entity
@Table(name = "users")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Setter
    private boolean active = false;

    @Column(nullable = false, unique = true)
    private String email;

    private String telephone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // ADMIN, AGENT, USER

    // Constructeur par défaut
    public Utilisateur() {}

    // Constructeur pratique
    public Utilisateur(String nom, String email, String password, Role role, String telephone) {
        this.nom = nom;
        this.email = email;
        this.password = password;
        this.role = role;
        this.telephone = telephone;
        this.active = true;
    }

    // Getters et setters
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return active;
    }
}
