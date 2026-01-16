#!/bin/bash

# Script alternatif d'initialisation de la base de données
# Essaie différentes méthodes pour donner les permissions

set -e

DB_NAME="digiticket"
DB_USER="digiticket_user"
DB_PASSWORD="digiticket123"
DB_HOST="localhost"

echo "═══════════════════════════════════════════════════════════════"
echo "  Configuration alternative de la base de données"
echo "═══════════════════════════════════════════════════════════════"
echo ""

# Méthode 1 : Avec sudo (si l'utilisateur a les droits sudo)
echo "📋 Tentative 1 : Avec sudo..."
if sudo -u postgres psql -d $DB_NAME -f src/main/resources/init-db.sql > /dev/null 2>&1; then
    echo "✅ Permissions configurées avec succès (méthode sudo)"
    PERMISSIONS_OK=true
else
    echo "❌ Échec avec sudo"
    PERMISSIONS_OK=false
fi

# Méthode 2 : Connexion locale sans mot de passe
if [ "$PERMISSIONS_OK" = false ]; then
    echo ""
    echo "📋 Tentative 2 : Connexion locale sans mot de passe..."
    if psql -U postgres -d $DB_NAME -f src/main/resources/init-db.sql > /dev/null 2>&1; then
        echo "✅ Permissions configurées avec succès (connexion locale)"
        PERMISSIONS_OK=true
    else
        echo "❌ Échec avec connexion locale"
    fi
fi

# Méthode 3 : Demander le mot de passe postgres
if [ "$PERMISSIONS_OK" = false ]; then
    echo ""
    echo "📋 Tentative 3 : Avec mot de passe postgres..."
    echo "⚠️  Le mot de passe 'digiticket123' est celui de digiticket_user, pas de postgres"
    echo ""
    read -sp "Entrez le mot de passe de l'utilisateur 'postgres' : " POSTGRES_PASSWORD
    echo ""
    
    export PGPASSWORD=$POSTGRES_PASSWORD
    if psql -h $DB_HOST -U postgres -d $DB_NAME -f src/main/resources/init-db.sql > /dev/null 2>&1; then
        echo "✅ Permissions configurées avec succès"
        PERMISSIONS_OK=true
    else
        echo "❌ Échec avec le mot de passe fourni"
    fi
    unset PGPASSWORD
fi

# Si aucune méthode n'a fonctionné
if [ "$PERMISSIONS_OK" = false ]; then
    echo ""
    echo "═══════════════════════════════════════════════════════════════"
    echo "⚠️  Impossible de configurer les permissions automatiquement"
    echo "═══════════════════════════════════════════════════════════════"
    echo ""
    echo "SOLUTIONS ALTERNATIVES :"
    echo ""
    echo "1. Trouver le mot de passe postgres :"
    echo "   - Vérifiez dans votre configuration PostgreSQL"
    echo "   - Ou réinitialisez-le avec : sudo passwd postgres"
    echo ""
    echo "2. Donner les permissions manuellement :"
    echo "   Connectez-vous en tant que postgres et exécutez :"
    echo ""
    echo "   psql -U postgres -d digiticket"
    echo "   GRANT ALL ON SCHEMA public TO digiticket_user;"
    echo "   GRANT ALL PRIVILEGES ON DATABASE digiticket TO digiticket_user;"
    echo ""
    echo "3. Laisser Hibernate créer les tables (si vous avez les permissions) :"
    echo "   Redémarrez simplement votre application Spring Boot"
    echo ""
    exit 1
fi

echo ""
echo "═══════════════════════════════════════════════════════════════"
echo "✅ Permissions configurées !"
echo "═══════════════════════════════════════════════════════════════"
echo ""
echo "Les tables seront créées automatiquement au prochain démarrage"
echo "de votre application Spring Boot grâce à Hibernate."
echo ""
echo "Redémarrez votre application maintenant !"
echo ""

