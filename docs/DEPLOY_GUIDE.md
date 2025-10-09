# 🚀 Guia de Desplegament - Sistema Seguretat

## 📋 **Checklist Pre-Desplegament**

### ✅ **Backend (Spring Boot)**
- [ ] Variables d'entorn configurades a Fly.io
- [ ] Base de dades PostgreSQL operativa
- [ ] Filtres de seguretat @Order correctes
- [ ] EXCLUDED_PATHS definits correctament
- [ ] Play Integrity tokens configurats

### ✅ **Android (Kotlin)**
- [ ] Version Code incrementat
- [ ] Play Integrity dependencies incloses
- [ ] Headers d'autenticació implementats
- [ ] Retry interceptor funcionant
- [ ] APK signat amb certificat de producció

### ✅ **Firebase/Google Play**
- [ ] Projecte Firebase configurat (1013719707047)
- [ ] SHA-1 del certificat de producció afegit
- [ ] Play Integrity API habilitada
- [ ] Google Play Console configurat

---

## 🛠️ **Procediment de Desplegament**

### **1. Backend Deploy**

```bash
# 1. Compilar
cd backend/
mvn clean package -DskipTests

# 2. Verificar configuració
fly secrets list

# 3. Desplegar
fly deploy

# 4. Verificar
curl https://validacio-backend.fly.dev/ping
curl https://validacio-backend.fly.dev/config/app
```

### **2. Android Build**

```bash
# 1. Netejar build
cd android/
./gradlew clean

# 2. Increment Version
# Editar: app/build.gradle.kts
releaseVersionCode = 10  # Incrementar
releaseVersionName = "1.2.0"  # Actualitzar

# 3. Build Release
./gradlew assembleRelease

# 4. Verificar APK
ls app/build/outputs/apk/release/
```

### **3. Variables d'Entorn**

```bash
# Play Integrity
fly secrets set PLAY_INTEGRITY_ENABLED=true
fly secrets set PLAY_INTEGRITY_TOKENS="real-tokens-here"
fly secrets set PLAY_INTEGRITY_CLOUD_PROJECT_NUMBER=1013719707047

# Control Versions
fly secrets set MIN_SUPPORTED_VERSION="1.2.0"
fly secrets set APP_UPDATE_MESSAGE="Nova versió disponible"

# Debug (deshabilitar en producció)
fly secrets set DEBUG_ENDPOINTS_ENABLED=false
```

---

## 🔐 **Configuració de Producció**

### **Eliminar Tokens Dummy**

```kotlin
// ❌ ELIMINAR d'IntegrityService.kt:
?: "dummy-token-for-testing" // TEMPORAL

// ✅ MANTENIR només:
val newToken = IntegrityService(context.applicationContext)
    .requestToken(cloudProjectNumber)
```

### **Configurar Tokens Reals**

```bash
# Obtenir tokens reals des de Google Play Console
# Configurar-los a Fly.io
fly secrets set PLAY_INTEGRITY_TOKENS="eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### **Logging de Producció**

```kotlin
// ApiClient.kt - Canviar a nivel mínim
private val logging = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.NONE  // Producció
    // level = HttpLoggingInterceptor.Level.BASIC  // Development
}
```

---

## 🧪 **Testing de Producció**

### **1. Verificar Endpoints**

```bash
# Config públic
curl https://validacio-backend.fly.dev/config/app

# Health check
curl https://validacio-backend.fly.dev/ping

# Endpoint protegit (hauria de fallar sense tokens)
curl https://validacio-backend.fly.dev/api/factures
```

### **2. Test amb App Real**

1. **Instal·lar APK** en dispositiu real
2. **Login** amb credencials vàlides
3. **Verificar** registre de dispositiu
4. **Provar** endpoints protegits
5. **Verificar** retry automàtic

### **3. Monitoratge**

```bash
# Logs en temps real
fly logs -a validacio-backend

# Filtrar errors Play Integrity
fly logs -a validacio-backend | grep "INVALID_PLAY_INTEGRITY"

# Verificar dispositius registrats
psql $DATABASE_URL -c "SELECT * FROM dispositius ORDER BY created_at DESC LIMIT 10;"
```

---

## 📱 **Google Play Console**

### **1. Preparar Release**

1. **Generar APK signat**
2. **Incrementar versionCode**
3. **Actualitzar changelogs**
4. **Configurar screenshots**

### **2. Upload a Play Console**

1. **App Bundle** recomanat sobre APK
2. **Testing intern** primer
3. **Testing tancat** amb beta testers
4. **Release gradual** 5% → 20% → 50% → 100%

### **3. Configurar Play Integrity**

1. **Verificar SHA-1** del certificat de upload
2. **Habilitar Play Integrity API**
3. **Configurar tokens** al backend
4. **Provar amb app de producció**

---

## 🚨 **Rollback Plan**

### **Si falla el Backend**

```bash
# 1. Rollback a versió anterior
fly releases list
fly releases rollback [RELEASE_ID]

# 2. Verificar rollback
curl https://validacio-backend.fly.dev/ping
```

### **Si falla l'Android**

1. **Aturar distribució** a Google Play Console
2. **Activar versió anterior** si cal
3. **Comunicar** als usuaris via update message
4. **Fix i re-deploy** urgent

### **Variables d'Emergència**

```bash
# Deshabilitar Play Integrity temporalment
fly secrets set PLAY_INTEGRITY_ENABLED=false

# Baixar versió mínima si cal
fly secrets set MIN_SUPPORTED_VERSION="1.1.8"

# Missatge d'emergència
fly secrets set APP_UPDATE_MESSAGE="Maintenance mode - please update"
```

---

## 📊 **Post-Deploy Monitoring**

### **Mètriques a Vigilar**

- **Response times** dels endpoints
- **Error rates** especial 401/403
- **Device registration** rates
- **Play Integrity token** failures
- **Version compliance** percentatges

### **Alertes Crítiques**

- **Token failure spike** > 10%
- **Device approval** queue > 50 pending
- **Version non-compliance** > 25%
- **Backend downtime** > 2 minutes

### **Dashboard URLs**

- **Fly.io Metrics**: `https://fly.io/apps/validacio-backend/monitoring`
- **Database Stats**: Via `psql` queries
- **Play Console**: `https://play.google.com/console/`

---

## 🔧 **Troubleshooting**

### **Common Issues**

| Problema | Símptomes | Solució |
|----------|-----------|---------|
| **Play Integrity Failing** | 401 errors massives | Verificar tokens, Firebase config |
| **Device Not Authorized** | 403 després de login | Aprovar dispositius a admin panel |
| **Version Too Old** | Update forçat | Incrementar MIN_SUPPORTED_VERSION |
| **JWT Expired** | 401 en requests autenticats | Verificar JWT secret i expiry |

### **Emergency Contacts**

- **Backend Issues**: Check Fly.io status
- **Android Issues**: Check Google Play Console
- **Database Issues**: Check PostgreSQL logs
- **Firebase Issues**: Check Firebase Console

---

## 📞 **Support**

- **Documentation**: `/docs/` folder
- **Issues**: GitHub Issues
- **Monitoring**: Fly.io Dashboard
- **Production**: Google Play Console

---

*Guia actualitzada: 9 Octubre 2025*  
*Versió: Android 1.1.9 + Backend Latest*