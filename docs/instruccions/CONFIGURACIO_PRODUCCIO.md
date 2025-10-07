# Configuració de Producció - Variables d'Entorn

## Variables d'Entorn necessàries per a Fly.io

### 1. Play Integrity (Control de seguretat Android)

```bash
# Activar/desactivar validació Play Integrity
fly secrets set PLAY_INTEGRITY_ENABLED=true

# Tokens Play Integrity vàlids (separats per comes)
# IMPORTANT: Aquests tokens s'han d'obtenir des de l'app Android real
fly secrets set PLAY_INTEGRITY_TOKENS="token1,token2,token3"
```

### 2. Control de Versions

```bash
# Versió mínima requerida de l'app
fly secrets set MIN_SUPPORTED_VERSION="1.2.0"

# Missatge personalitzat d'actualització
fly secrets set APP_UPDATE_MESSAGE="Cal actualitzar l'aplicació per continuar. Versió mínima: 1.2.0"

# URL de descàrrega de l'app (Play Store)
fly secrets set APP_UPDATE_URL="https://play.google.com/store/apps/details?id=cat.ajterrassa.validaciofactures"
```

### 3. Configurar variables existents (si no ho has fet)

```bash
# Base de dades
fly secrets set SPRING_DATASOURCE_URL="jdbc:mariadb://..."
fly secrets set SPRING_DATASOURCE_USERNAME="..."
fly secrets set SPRING_DATASOURCE_PASSWORD="..."

# JWT
fly secrets set JWT_SECRET="..."

# AWS S3
fly secrets set AWS_ACCESS_KEY_ID="..."
fly secrets set AWS_SECRET_ACCESS_KEY="..."
fly secrets set AWS_S3_BUCKET="..."
```

## Com obtenir tokens Play Integrity reals

### Pas 1: Configurar Play Integrity a la consola de Google

1. Anar a [Google Play Console](https://play.google.com/console)
2. Seleccionar la teva app
3. Anar a **"Release" > "Setup" > "App integrity"**
4. Activar **Play Integrity API**
5. Obtenir les claus d'API

### Pas 2: Des de l'app Android

L'app Android ha de:

1. **Generar un nonce únic** per cada petició
2. **Sol·licitar token** a Play Integrity API amb el nonce
3. **Enviar el token** al backend via header `X-Play-Integrity-Token`

### Pas 3: Backend valida el token

El backend:
1. **Verifica la signatura** del token
2. **Valida el nonce**
3. **Comprova els veredictes** (app i dispositiu)

## Configuracions per a desenvolupament local

### application.yml (local)

```yaml
play:
  integrity:
    validation-enabled: false  # Desactivat per desenvolupament
    accepted-tokens: ""

app:
  min:
    supported:
      version: "0.0.1"  # Permetre versions molt antigues en desenvolupament
```

## Comandes útils per gestionar versions

### Veure configuració actual
```bash
fly status
fly secrets list
```

### Actualitzar versió mínima
```bash
# Forçar actualització a versió 1.3.0
fly secrets set MIN_SUPPORTED_VERSION="1.3.0"

# Redeploy per aplicar canvis
fly deploy
```

### Monitoritzar logs
```bash
fly logs
```

## Flux de llançament recomanat

1. **Desenvolupament** → Play Integrity desactivat
2. **Staging** → Play Integrity activat amb tokens de test
3. **Producció** → Play Integrity activat amb tokens reals

### Procediment de llançament gradual

1. **Pujar nova versió** a Play Store (internal testing)
2. **Configurar versió mínima** al backend (encara no forçar)
3. **Provar amb beta testers**
4. **Llançament gradual** (5% → 25% → 50% → 100%)
5. **Forçar versió mínima** quan adopció sigui >80%

## Troubleshooting

### Error: Play Integrity token invalid
- Verificar que els tokens siguin dels correctes
- Comprovar que el nonce sigui únic
- Validar signatura de l'app a Play Console

### Error: Version too old
- Verificar `MIN_SUPPORTED_VERSION` al backend
- Comprovar que l'app envii el header `X-App-Version` correcte

### App no pot accedir (403)
- Verificar que el dispositiu estigui aprovat a la pantalla d'admin
- Comprovar que l'app envii el header `X-Firebase-Installation-Id`