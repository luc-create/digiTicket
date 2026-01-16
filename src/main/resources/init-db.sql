-- Script d'initialisation de la base de données
-- À exécuter en tant qu'administrateur PostgreSQL (postgres)
-- Commande: psql -h localhost -U postgres -d digiticket -f init-db.sql

-- Donner les permissions nécessaires à l'utilisateur
GRANT ALL ON SCHEMA public TO digiticket_user;
GRANT ALL PRIVILEGES ON DATABASE digiticket TO digiticket_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO digiticket_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO digiticket_user;

