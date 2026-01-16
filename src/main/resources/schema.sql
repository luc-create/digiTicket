-- Script de création des tables pour DigiTicket
-- À exécuter manuellement dans PostgreSQL si Hibernate ne crée pas automatiquement les tables
-- Commande: psql -h localhost -U digiticket_user -d digiticket -f schema.sql

-- Supprimer les tables si elles existent (ATTENTION: supprime toutes les données)
DROP TABLE IF EXISTS admin_logs CASCADE;
DROP TABLE IF EXISTS notifications CASCADE;
DROP TABLE IF EXISTS tickets CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Créer la table users
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT false,
    telephone VARCHAR(255),
    role VARCHAR(50) NOT NULL
);

-- Créer la table tickets
CREATE TABLE tickets (
    id SERIAL PRIMARY KEY,
    titre VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    statut VARCHAR(50) NOT NULL,
    date_creation TIMESTAMP NOT NULL,
    client_id INTEGER NOT NULL,
    agent_id INTEGER,
    FOREIGN KEY (client_id) REFERENCES users(id),
    FOREIGN KEY (agent_id) REFERENCES users(id)
);

-- Créer la table notifications
CREATE TABLE notifications (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    ticket_id INTEGER,
    titre VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    lue BOOLEAN NOT NULL DEFAULT false,
    date_creation TIMESTAMP NOT NULL,
    type VARCHAR(50) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (ticket_id) REFERENCES tickets(id)
);

-- Créer la table admin_logs
CREATE TABLE admin_logs (
    id SERIAL PRIMARY KEY,
    admin_id INTEGER NOT NULL,
    action VARCHAR(255) NOT NULL,
    details TEXT,
    date_action TIMESTAMP NOT NULL,
    target_user_id INTEGER,
    target_ticket_id INTEGER,
    FOREIGN KEY (admin_id) REFERENCES users(id),
    FOREIGN KEY (target_user_id) REFERENCES users(id),
    FOREIGN KEY (target_ticket_id) REFERENCES tickets(id)
);
