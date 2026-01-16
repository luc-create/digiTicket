# Guide : Comment recevoir la liste des utilisateurs

## 📋 Endpoints disponibles

### 1. GET `/api/admin/users`
- **Description** : Retourne tous les utilisateurs
- **Permissions** : Authentification + Rôle ADMIN requis
- **Réponse** : Liste de tous les utilisateurs

### 2. GET `/api/utilisateurs`
- **Description** : Retourne tous les utilisateurs (même fonctionnalité)
- **Permissions** : Authentification + Rôle ADMIN requis
- **Réponse** : Liste de tous les utilisateurs

### 3. GET `/api/utilisateurs/actifs`
- **Description** : Retourne uniquement les utilisateurs avec compte actif
- **Permissions** : Authentification + Rôle ADMIN requis
- **Réponse** : Liste des utilisateurs actifs

## 🔐 Étapes pour accéder à ces endpoints

### Étape 1 : Créer un compte ADMIN

#### Option A : Via Swagger UI (recommandé)

1. Allez sur `/api/auth/register` dans Swagger UI
2. Utilisez cet exemple :
```json
{
  "nom": "Admin Test",
  "email": "admin@test.com",
  "password": "password123",
  "role": "ADMIN"
}
```
3. Exécutez la requête

#### Option B : Via curl

```bash
curl -X 'POST' \
  'http://localhost:8080/api/auth/register' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "nom": "Admin Test",
  "email": "admin@test.com",
  "password": "password123",
  "role": "ADMIN"
}'
```

### Étape 2 : Se connecter pour obtenir un token JWT

#### Via Swagger UI

1. Allez sur `/api/auth/login`
2. Utilisez cet exemple :
```json
{
  "email": "admin@test.com",
  "password": "password123"
}
```
3. Exécutez la requête
4. **Copiez le token** de la réponse (champ `token`)

#### Réponse attendue :
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbkB0ZXN0LmNvbSIsImlhdCI6MTYzOTc2ODAwMCwiZXhwIjoxNjM5ODU0NDAwfQ...",
  "type": "Bearer",
  "user": {
    "id": 1,
    "email": "admin@test.com",
    "nom": "Admin Test",
    "role": "ADMIN"
  }
}
```

### Étape 3 : Utiliser le token pour accéder aux endpoints

#### Dans Swagger UI

1. Cliquez sur le bouton **"Authorize"** (🔒 en haut à droite de Swagger UI)
2. Dans le champ "Value", entrez : `Bearer <votre_token>`
   - Exemple : `Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbkB0ZXN0LmNvbS...`
3. Cliquez sur **"Authorize"**
4. Cliquez sur **"Close"**
5. Maintenant vous pouvez appeler `GET /api/admin/users` ou `GET /api/utilisateurs`

#### Avec curl

```bash
curl -X 'GET' \
  'http://localhost:8080/api/admin/users' \
  -H 'accept: application/json' \
  -H 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbkB0ZXN0LmNvbS...'
```

## 🔍 Vérification

Si tout fonctionne, vous devriez recevoir une réponse JSON avec la liste des utilisateurs :

```json
[
  {
    "id": 1,
    "nom": "Jean Dupont",
    "email": "jean.dupont@test.com",
    "role": "CLIENT",
    "active": false,
    "telephone": null
  },
  {
    "id": 2,
    "nom": "Admin Test",
    "email": "admin@test.com",
    "role": "ADMIN",
    "active": true,
    "telephone": null
  }
]
```

## ⚠️ Erreurs courantes

### Erreur 403 Forbidden
- **Cause** : Vous n'êtes pas authentifié ou votre compte n'a pas le rôle ADMIN
- **Solution** : 
  1. Vérifiez que vous avez bien cliqué sur "Authorize" dans Swagger
  2. Vérifiez que votre token est valide
  3. Vérifiez que votre compte a le rôle ADMIN

### Erreur 401 Unauthorized
- **Cause** : Token invalide ou expiré
- **Solution** : Reconnectez-vous pour obtenir un nouveau token

## 💡 Astuce

Le token JWT est valide pendant 24 heures par défaut. Vous n'avez pas besoin de vous reconnecter à chaque requête, mais vous devrez renouveler le token après expiration.

