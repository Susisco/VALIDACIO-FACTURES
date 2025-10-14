# 📱 Guia Completa de Release Android - Validació Factures

> **Última actualització**: 14 octubre 2025  
> **Versió del document**: 2.0  
> **Aplicable a**: Android App v1.2.x+

## 📖 **Índex**

1. [Visió General](#-visió-general)
2. [Checklist Complet](#-checklist-complet)
3. [Preparació de la Versió](#-preparació-de-la-versió)
4. [Build de Producció](#️-build-de-producció)
5. [Deploy del Backend](#-deploy-del-backend)
6. [Publicació a Play Console](#-publicació-a-play-console)
7. [Verificació i Testing](#-verificació-i-testing)
8. [Troubleshooting](#-troubleshooting)
9. [Referències](#-referències)

---

## 🎯 **Visió General**

Aquest document descriu el procés complet per crear, compilar i publicar una nova versió de l'aplicació Android "Validació Factures i Albarans" a Google Play Store.

### **Components del Sistema**
- **Android App**: Kotlin + Jetpack Compose
- **Backend**: Spring Boot (Fly.io)
- **Base de Dades**: PostgreSQL (Fly.io)
- **Autenticació**: JWT + Play Integrity
- **Publicació**: Google Play Console

---

## ✅ **Checklist Complet**

### **📱 FASE 1: PREPARACIÓ VERSIÓ**
- [ ] **1.1** Incrementar `versionCode` (+1 del anterior)
- [ ] **1.2** Incrementar `versionName` (semàntic: MAJOR.MINOR.PATCH)
- [ ] **1.3** Verificar que no hi hagi errors de compilació
- [ ] **1.4** Revisar i actualitzar dependències si cal
- [ ] **1.5** Provar funcionalitat crítica en local (opcional)

### **🏗️ FASE 2: BUILD DE PRODUCCIÓ**
- [ ] **2.1** Netejar builds anteriors (`./gradlew clean`)
- [ ] **2.2** Verificar configuració de signing
- [ ] **2.3** Generar AAB de producció (`./gradlew bundleProdRelease`)
- [ ] **2.4** Verificar mida i integritat de l'AAB
- [ ] **2.5** Guardar AAB en lloc segur

### **🚀 FASE 3: BACKEND SERVIDOR**
- [ ] **3.1** Verificar status del repositori Git
- [ ] **3.2** Commit canvis locals si n'hi ha
- [ ] **3.3** Deploy al servidor Fly.io (`fly deploy`)
- [ ] **3.4** Verificar endpoints crítics funcionant
- [ ] **3.5** Comprovar logs del servidor

### **📤 FASE 4: PUBLICACIÓ PLAY CONSOLE**
- [ ] **4.1** Accedir a Google Play Console
- [ ] **4.2** Crear nova versió (track adequat)
- [ ] **4.3** Pujar AAB i verificar upload
- [ ] **4.4** Afegir notes de versió (format `<ca>`)
- [ ] **4.5** Revisar i corregir errors de validació
- [ ] **4.6** Publicar a track de testing
- [ ] **4.7** Promocionar a producció (quan estigui validat)

### **✅ FASE 5: VERIFICACIÓ POST-RELEASE**
- [ ] **5.1** Confirmar disponibilitat de la versió
- [ ] **5.2** Descarregar i instal·lar des de Play Store
- [ ] **5.3** Verificar JWT i Play Integrity funcionant
- [ ] **5.4** Provar upload de fotos i documents
- [ ] **5.5** Actualitzar documentació si cal

---

## 📱 **Preparació de la Versió**

### **1.1 Actualització de Números de Versió**

Edita `android/app/build.gradle.kts`:

```kotlin
// 👉 Incrementa a cada release que pugis a Play
val releaseVersionCode = 14  // ← Incrementar +1
val releaseVersionName = "1.2.4"  // ← Versió semàntica
```

### **1.2 Esquema de Versionat Semàntic**

- **MAJOR** (1.x.x): Canvis incompatibles de l'API
- **MINOR** (x.2.x): Nova funcionalitat compatible cap enrere
- **PATCH** (x.x.4): Correccions d'errors

### **1.3 Verificació de Compilació**

```bash
cd android/
./gradlew assembleDebug
```

Si hi ha errors, soluciona'ls abans de continuar.

---

## 🏗️ **Build de Producció**

### **2.1 Neteja de Builds Anteriors**

```powershell
cd android
.\gradlew clean
```

### **2.2 Verificació de Configuració de Signing**

Assegura't que tens configurat:
- `keystore.properties` amb credencials correctes
- O variables d'entorn `UPLOAD_*`

### **2.3 Generació de l'AAB**

```powershell
.\gradlew bundleProdRelease
```

### **2.4 Verificació de l'AAB**

L'AAB es genera a:
```
app\build\outputs\bundle\prodRelease\app-prod-release.aab
```

**Verificacions**:
- Mida del fitxer: ~30-40MB (acceptable)
- Data de creació: acabat de generar
- No errors en el log de build

---

## 🚀 **Deploy del Backend**

### **3.1 Verificació del Repositori**

```powershell
cd backend
git status
```

### **3.2 Commit de Canvis (si cal)**

```powershell
git add .
git commit -m "📱 Preparació versió Android 1.2.4"
git push origin main
```

### **3.3 Deploy a Fly.io**

```powershell
fly deploy
```

### **3.4 Verificació del Deploy**

```powershell
# Verificar estat
fly status

# Provar endpoints crítics
curl https://validacio-backend.fly.dev/health
curl https://validacio-backend.fly.dev/config/app
```

### **3.5 Logs del Servidor**

```powershell
fly logs --tail
```

---

## 📤 **Publicació a Play Console**

### **4.1 Accés a Play Console**

1. Ves a [Google Play Console](https://play.google.com/console)
2. Selecciona l'app "Validació Factures i Albarans"
3. Navega a **Prova i llança** → **Darreres versions i paquets**

### **4.2 Creació de Nova Versió**

1. Clica **Crea una versió de prova tancada** (recomanat per testing)
2. O **Crea una versió de prova oberta** (per més testers)

### **4.3 Upload de l'AAB**

1. Arrossega l'AAB al camp corresponent
2. Espera que es completi l'upload i processament
3. Verifica que no hi hagi errors de validació

### **4.4 Notes de la Versió**

**Format obligatori amb etiquetes d'idioma**:

```xml
<ca>
Millores versió 1.2.4:

• Solucionats errors 403 en pujada de fotos d'albarans
• Millor gestió automàtica de tokens de seguretat JWT
• Optimització del sistema Play Integrity
• Correccions d'errors de connexió amb el servidor
• Millor estabilitat i rendiment general

Actualització recomanada per a tots els usuaris.
</ca>
```

### **4.5 Resolució d'Errors Comuns**

- **"Text fora d'etiquetes d'idioma"**: Tot el text ha d'estar dins `<ca>...</ca>`
- **"Descripció massa llarga"**: Redueix el text mantenint la informació essencial
- **"App encara no es pot publicar"**: Revisa el **Tauler de control** per completar configuració

### **4.6 Publicació**

1. Clica **Desa** per guardar l'esborrany
2. Clica **Revisa la versió** per validar
3. Clica **Inicia el llançament** per publicar

---

## ✅ **Verificació i Testing**

### **5.1 Descàrrega des de Play Store**

1. Accedeix al testing track configurat
2. Descarrega l'app en un dispositiu de prova
3. Verifica el número de versió a "Configuració" → "Sobre l'app"

### **5.2 Testing de Funcionalitat Crítica**

- [ ] **Login**: Autenticació d'usuari
- [ ] **JWT**: Renovació automàtica de tokens
- [ ] **Play Integrity**: Validació de dispositiu
- [ ] **Upload fotos**: Pujada de documents d'albarans
- [ ] **Navegació**: Totes les pantalles principals

### **5.3 Logs i Debugging**

Si hi ha problemes:
```bash
# Logs del backend
fly logs --tail

# Logs d'Android (si tens accés a logcat)
adb logcat | grep "ValidacioFactures"
```

---

## 🚨 **Troubleshooting**

### **Problemes Comuns de Build**

| Error | Solució |
|-------|---------|
| `Signing config 'upload' incompleta` | Verifica `keystore.properties` o variables d'entorn |
| `Failed to transform app-prod-release.aab` | Executa `./gradlew clean` i torna a provar |
| `Compilation failed` | Revisa errors de sintaxi o dependències |

### **Problemes de Play Console**

| Error | Solució |
|-------|---------|
| "Notes de versió massa llargues" | Redueix text a <500 caràcters |
| "Text fora d'etiquetes" | Assegura't que tot està dins `<ca>...</ca>` |
| "Bundle no vàlid" | Regenera l'AAB amb signing correcte |

### **Problemes de Backend**

| Error | Solució |
|-------|---------|
| `fly deploy` falla | Verifica que estàs al directori `backend/` |
| Endpoint retorna 500 | Revisa logs amb `fly logs` |
| Base de dades inaccessible | Verifica configuració PostgreSQL |

---

## 📚 **Referències**

### **Documents Relacionats**
- [`android/docs/RELEASE_PROCESS.md`](../android/docs/RELEASE_PROCESS.md) - Procés detallat de release
- [`android/docs/PLAY_CONSOLE_SETUP.md`](../android/docs/PLAY_CONSOLE_SETUP.md) - Configuració inicial Play Console
- [`docs/DEPLOY_GUIDE.md`](DEPLOY_GUIDE.md) - Guia general de desplegament
- [`docs/desplegament/DEPLOY_FLYIO.md`](desplegament/DEPLOY_FLYIO.md) - Desplegament específic Fly.io

### **Recursos Externs**
- [Google Play Console](https://play.google.com/console)
- [Fly.io Dashboard](https://fly.io/dashboard)
- [Android Developer - App Bundles](https://developer.android.com/guide/app-bundle)
- [Play Integrity API](https://developer.android.com/google/play/integrity)

### **Configuració del Projecte**
- **Package Name**: `com.ajterrassa.validaciofacturesalbarans`
- **Firebase Project**: `1013719707047`
- **Backend URL**: `https://validacio-backend.fly.dev/`
- **Repository**: `https://github.com/Susisco/VALIDACIO-FACTURES`

---

## 📝 **Changelog del Document**

- **v2.0** (14/10/2025): Creació de guia completa unificada
- **v1.x** (anterior): Documents separats RELEASE_PROCESS i PLAY_CONSOLE_SETUP

---

**💡 Consell**: Guarda aquest document com a referència i actualitza'l amb cada nova experiència o canvi en el procés.