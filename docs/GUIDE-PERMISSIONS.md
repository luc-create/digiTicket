# Guide : Donner les permissions PostgreSQL

## ⚠️ Problème
Le mot de passe "digiticket123" est celui de `digiticket_user`, **PAS** celui de `postgres` (administrateur).

## ✅ Solution : Donner les permissions manuellement

### Méthode 1 : Se connecter à PostgreSQL et exécuter les commandes

1. **Connectez-vous à PostgreSQL en tant que postgres :**
   ```bash
   psql -U postgres -d digiticket
   ```
   (Vous devrez entrer le mot de passe de l'utilisateur postgres)

2. **Une fois connecté, exécutez ces commandes SQL :**
   ```sql
   GRANT ALL ON SCHEMA public TO digiticket_user;
   GRANT ALL PRIVILEGES ON DATABASE digiticket TO digiticket_user;
   ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO digiticket_user;
   ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO digiticket_user;
   ```

3. **Quittez PostgreSQL :**
   ```sql
   \q
   ```

### Méthode 2 : Exécuter le script directement

Si vous connaissez le mot de passe postgres, remplacez `VOTRE_MOT_DE_PASSE_POSTGRES` :

```bash
PGPASSWORD=VOTRE_MOT_DE_PASSE_POSTGRES psql -h localhost -U postgres -d digiticket -f src/main/resources/init-db.sql
```

### Méthode 3 : Avec sudo (si vous avez les droits)

```bash
sudo -u postgres psql -d digiticket -f src/main/resources/init-db.sql
```

## 🔍 Comment trouver le mot de passe postgres ?

1. **Vérifiez dans votre configuration PostgreSQL**
2. **Ou réinitialisez-le :**
   ```bash
   sudo passwd postgres
   ```

## ✅ Après avoir donné les permissions

Une fois les permissions données, vous avez deux options :

### Option A : Laisser Hibernate créer les tables (RECOMMANDÉ)
Redémarrez simplement votre application Spring Boot. Hibernate créera automatiquement toutes les tables.

### Option B : Créer les tables manuellement
```bash
PGPASSWORD=digiticket123 psql -h localhost -U digiticket_user -d digiticket -f src/main/resources/schema.sql
```

## 🔍 Vérification

Pour vérifier que les permissions sont correctes :
```bash
PGPASSWORD=digiticket123 psql -h localhost -U digiticket_user -d digiticket -c "SELECT has_schema_privilege('digiticket_user', 'public', 'CREATE');"
```

Si cela retourne `t` (true), les permissions sont correctes !

