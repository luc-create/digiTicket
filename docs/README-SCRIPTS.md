# Guide d'exécution des scripts SQL

## Étape 1 : Donner les permissions (en tant qu'administrateur)

Exécutez le script `init-db.sql` en tant qu'administrateur PostgreSQL (utilisateur `postgres`) :

```bash
cd "/home/luc/StudioProjects/Spring Boot/backend"
psql -h localhost -U postgres -d digiticket -f src/main/resources/init-db.sql
```

**Note :** Vous devrez entrer le mot de passe de l'utilisateur `postgres` quand il sera demandé.

**Alternative :** Si vous connaissez le mot de passe, utilisez la variable d'environnement :
```bash
PGPASSWORD=votre_mot_de_passe_postgres psql -h localhost -U postgres -d digiticket -f src/main/resources/init-db.sql
```

## Étape 2 : Créer les tables (avec l'utilisateur digiticket_user)

Après avoir donné les permissions, exécutez le script `schema.sql` :

```bash
cd "/home/luc/StudioProjects/Spring Boot/backend"
PGPASSWORD=digiticket123 psql -h localhost -U digiticket_user -d digiticket -f src/main/resources/schema.sql
```

## Alternative : Laisser Hibernate créer les tables automatiquement

Si vous avez donné les permissions à l'étape 1, vous pouvez simplement **redémarrer votre application Spring Boot**. Hibernate créera automatiquement toutes les tables grâce à la configuration `ddl-auto: update` dans `application.yml`.

## Vérification

Pour vérifier que les tables ont été créées :

```bash
PGPASSWORD=digiticket123 psql -h localhost -U digiticket_user -d digiticket -c "\dt"
```

Cette commande liste toutes les tables de la base de données.

## Résolution de problèmes

### Si vous n'avez pas accès à l'utilisateur postgres :
Vous pouvez essayer de vous connecter directement avec l'utilisateur `digiticket_user` et exécuter les commandes SQL manuellement :

```bash
PGPASSWORD=digiticket123 psql -h localhost -U digiticket_user -d digiticket
```

Puis dans le prompt PostgreSQL, exécutez :
```sql
GRANT ALL ON SCHEMA public TO digiticket_user;
GRANT ALL PRIVILEGES ON DATABASE digiticket TO digiticket_user;
```

Ensuite, créez les tables avec le script `schema.sql` ou laissez Hibernate les créer au démarrage.

