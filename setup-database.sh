#!/bin/bash

# Script d'initialisation de la base de données DigiTicket
# Ce script configure les permissions et crée les tables

set -e

echo "═══════════════════════════════════════════════════════════════"
echo "  Configuration de la base de données DigiTicket"
echo "═══════════════════════════════════════════════════════════════"
echo ""

# Couleurs pour les messages
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

DB_NAME="digiticket"
DB_USER="digiticket_user"
DB_PASSWORD="digiticket123"
DB_HOST="localhost"

# Étape 1 : Donner les permissions
echo "📋 ÉTAPE 1 : Configuration des permissions..."
echo ""
echo -e "${YELLOW}⚠️  Cette étape nécessite les droits administrateur PostgreSQL${NC}"
echo ""
read -p "Avez-vous le mot de passe de l'utilisateur 'postgres' ? (o/n) " -n 1 -r
echo ""

if [[ $REPLY =~ ^[OoYy]$ ]]; then
    read -sp "Entrez le mot de passe postgres: " POSTGRES_PASSWORD
    echo ""
    echo ""
    
    export PGPASSWORD=$POSTGRES_PASSWORD
    if psql -h $DB_HOST -U postgres -d $DB_NAME -f src/main/resources/init-db.sql > /dev/null 2>&1; then
        echo -e "${GREEN}✅ Permissions configurées avec succès${NC}"
    else
        echo -e "${RED}❌ Erreur lors de la configuration des permissions${NC}"
        echo "Essayez d'exécuter manuellement :"
        echo "  psql -h localhost -U postgres -d digiticket -f src/main/resources/init-db.sql"
        exit 1
    fi
    unset PGPASSWORD
else
    echo -e "${YELLOW}⚠️  Étape 1 ignorée. Assurez-vous d'avoir donné les permissions manuellement.${NC}"
    echo "Commande à exécuter :"
    echo "  psql -h localhost -U postgres -d digiticket -f src/main/resources/init-db.sql"
    echo ""
    read -p "Appuyez sur Entrée pour continuer..." 
fi

echo ""
echo "═══════════════════════════════════════════════════════════════"
echo ""

# Étape 2 : Créer les tables
echo "📋 ÉTAPE 2 : Création des tables..."
echo ""
echo "Choisissez une option :"
echo "  1) Créer les tables manuellement avec le script SQL"
echo "  2) Laisser Hibernate créer les tables automatiquement (recommandé)"
echo ""
read -p "Votre choix (1 ou 2) : " -n 1 -r
echo ""
echo ""

if [[ $REPLY =~ ^[1]$ ]]; then
    echo "Création des tables avec le script SQL..."
    export PGPASSWORD=$DB_PASSWORD
    if psql -h $DB_HOST -U $DB_USER -d $DB_NAME -f src/main/resources/schema.sql; then
        echo ""
        echo -e "${GREEN}✅ Tables créées avec succès${NC}"
    else
        echo -e "${RED}❌ Erreur lors de la création des tables${NC}"
        exit 1
    fi
    unset PGPASSWORD
else
    echo -e "${GREEN}✅ Configuration terminée${NC}"
    echo ""
    echo "Les tables seront créées automatiquement au prochain démarrage de l'application Spring Boot."
    echo "Redémarrez votre application maintenant !"
fi

echo ""
echo "═══════════════════════════════════════════════════════════════"
echo ""

# Vérification
echo "🔍 Vérification des tables..."
export PGPASSWORD=$DB_PASSWORD
TABLES=$(psql -h $DB_HOST -U $DB_USER -d $DB_NAME -t -c "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public';" 2>/dev/null | xargs)
unset PGPASSWORD

if [ "$TABLES" -gt "0" ]; then
    echo -e "${GREEN}✅ $TABLES table(s) trouvée(s) dans la base de données${NC}"
    echo ""
    echo "Tables existantes :"
    export PGPASSWORD=$DB_PASSWORD
    psql -h $DB_HOST -U $DB_USER -d $DB_NAME -c "\dt"
    unset PGPASSWORD
else
    echo -e "${YELLOW}⚠️  Aucune table trouvée. Les tables seront créées au démarrage de l'application.${NC}"
fi

echo ""
echo -e "${GREEN}✅ Configuration terminée !${NC}"
echo ""

