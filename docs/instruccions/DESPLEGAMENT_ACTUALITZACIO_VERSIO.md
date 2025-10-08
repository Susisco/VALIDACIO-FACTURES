# Desplegament d'Actualització de Versió - App Android

Aquest document descriu el procediment complet per desplegar una nova versió de l'aplicació Android amb integració Firebase i sistema de control de versions.

---

## 📋 **Checklist previ**

Abans de començar, assegura't que tens:

- [ ] **Android Studio** configurat amb el projecte
- [ ] **Keystore** de signatura accessible
- [ ] **Accés a Google Play Console**
- [ ] **Accés a Fly.io** (per actualitzar backend)
- [ ] **Firebase Console** configurat
- [ ] **Versions anteriors** funcionant correctament

---

## 🚀 **Pas 1: Preparar el projecte Android**

### 1.1 Actualitzar les versions

Edita `app/build.gradle.kts`:

```kotlin
// 👉 Incrementa a cada release que pugis a Play
val releaseVersionCode = 8          // ← Incrementa +1
val releaseVersionName = "1.1.8"    // ← Actualitza coherentment
```

**Regla**: `versionCode` sempre ha de créixer (7→8→9...), `versionName` segueix semantic versioning.

### 1.2 Verificar configuració Firebase

Comprova que tens:

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.kapt")
    id("com.google.gms.google-services")  // ← IMPRESCINDIBLE
}
```

### 1.3 Verificar dependències Firebase

```kotlin
dependencies {
    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.3.0"))
    implementation("com.google.firebase:firebase-installations-ktx")
    
    // Play Integrity + App Update
    implementation("com.google.android.play:integrity:1.3.0")
    implementation("com.google.android.play:app-update:2.1.0")
    implementation("com.google.android.play:app-update-ktx:2.1.0")
}
```

### 1.4 Verificar google-services.json

- **Ubicació**: `app/google-services.json`
- **Package name**: Ha de coincidir amb `com.ajterrassa.validaciofacturesalbarans`
- **Project Number**: Verificar que és `1013719707047`

---

## 🏗️ **Pas 2: Compilar l'aplicació**

### 2.1 Neteja i sincronització

```bash
# A Android Studio
Build → Clean Project
Build → Rebuild Project
```

### 2.2 Generar AAB signat

1. **Build → Generate Signed Bundle/APK**
2. Selecciona **Android App Bundle (AAB)**
3. **Keystore path**: Selecciona el keystore de producció
4. **Key alias**: Usa l'alias de signatura
5. **Build variant**: **prodRelease**
6. **Destinació**: Anota on es desa l'AAB

### 2.3 Verificar l'AAB generat

- **Nom fitxer**: `app-prod-release.aab`
- **Mida**: Aproximadament 10-50MB
- **Ubicació**: `app/release/`

---

## 📱 **Pas 3: Pujar al Play Store**

### 3.1 Accedir a Play Console

1. Ves a [Google Play Console](https://play.google.com/console/)
2. Selecciona **"EnviarAlbara"** (o nom de la teva app)
3. Ves a **"Distribución" → "Release en producción"**

### 3.2 Crear nova release

1. **Clica "Crear nueva release"**
2. **Puja l'AAB**: Arrossega l'arxiu generat
3. **Espera** que es processi i verifiqui

### 3.3 Omple informació de la release

**Nom de la release**: `Versió 1.1.8`

**Notes de la versió** (exemple):
```
Versió 1.1.8:
• Integració amb Firebase per seguretat millorada
• Control de versions automàtic 
• Registre de dispositius optimitzat
• Validació d'integritat amb Play Integrity
• Millores de rendiment i estabilitat

Requisits: Versió mínima Android 7.0 (API 24)
```

### 3.4 Configurar desplegament

- **Tipus**: **Desplegament complet** (100% usuaris)
- **Aprovació**: **Automàtica** (si no hi ha issues)
- **Temporització**: **Immediata**

### 3.5 Revisar i publicar

1. **Revisa** tota la informació
2. **Clica "Revisar release"**
3. **Clica "Iniciar distribución en producción"**
4. **Confirma** la publicació

---

## ⚙️ **Pas 4: Actualitzar backend**

### 4.1 Actualitzar versió mínima (opcional)

Si vols forçar que tots utilitzin la nova versió:

```bash
fly secrets set MIN_SUPPORTED_VERSION="1.1.8"
```

### 4.2 Monitoritzar logs

```bash
fly logs --app validacio-backend
```

### 4.3 Verificar secrets actuals

```bash
fly secrets list
```

Comprova que tens configurats:
- `PLAY_INTEGRITY_PROJECT_NUMBER`
- `MIN_SUPPORTED_VERSION`
- `APP_UPDATE_MESSAGE`
- `APP_UPDATE_URL`

---

## 🔍 **Pas 5: Verificació i proves**

### 5.1 Esperar aprovació Google

- **Temps**: 1-3 hores per apps existents
- **Estat**: Comprova a Play Console
- **Notificació**: Rebràs email quan estigui live

### 5.2 Proves amb dispositiu real

1. **Actualitza** l'app des del Play Store
2. **Obre l'aplicació** → Ha de funcionar sense errors
3. **Comprova** que la versió és la correcta
4. **Verifica** que es registra automàticament

### 5.3 Verificar panell d'administració

1. **Accedeix** a `https://validacio-backend.fly.dev/`
2. **Login** com a administrador
3. **Ves a DISPOSITIUS** → Has de veure nous registres
4. **Aprova** dispositius pendents si cal

### 5.4 Provar control de versions

Amb un dispositiu amb versió antiga:
- **Espertat**: ERROR 426 + missatge d'actualització
- **Acció**: L'app ha de redirigir al Play Store

---

## 📊 **Pas 6: Monitoritzar desplegament**

### 6.1 Mètriques de Play Console

- **% d'adopció** de la nova versió
- **Crashes** i errors reportats
- **Valoracions** i comentaris

### 6.2 Logs del backend

```bash
# Monitoritzar registres de dispositius
fly logs --app validacio-backend | grep "device.*register"

# Monitoritzar errors de versió
fly logs --app validacio-backend | grep "426"
```

### 6.3 Panell de dispositius

- **Dispositius pendents**: Aprovar manualment
- **Dispositius actius**: Verificar versions
- **Estadístiques**: Versions més usades

---

## ⚠️ **Resolució de problemes**

### Error: "App not signed correctly"
- **Causa**: Keystore incorrecte
- **Solució**: Verificar keystore i alias

### Error: "Version code must be greater"
- **Causa**: `versionCode` no incrementat
- **Solució**: Incrementar `versionCode` a valor superior

### Error: "Google services plugin not applied"
- **Causa**: Plugin no afegit o mal configurat
- **Solució**: Verificar plugins al `build.gradle.kts`

### App no es registra al backend
- **Causa**: Firebase mal configurat
- **Solució**: Verificar `google-services.json` i package name

### 426 Upgrade Required massa agressiu
- **Causa**: `MIN_SUPPORTED_VERSION` massa alt
- **Solució**: Baixar temporalment la versió mínima

---

## 📝 **Checklist final**

Abans de donar per acabat el desplegament:

- [ ] **AAB generat** i pujat correctament
- [ ] **Play Store** mostra la nova versió com activa
- [ ] **Backend actualitzat** amb nova versió mínima (opcional)
- [ ] **Logs** no mostren errors crítics
- [ ] **Dispositius de prova** funcionen correctament
- [ ] **Panell admin** accessible i operatiu
- [ ] **Documentació** actualitzada amb nova versió

---

## 📞 **Contacte i suport**

En cas de problemes:

1. **Logs del backend**: `fly logs --app validacio-backend`
2. **Documentació tècnica**: `docs/instruccions/`
3. **Firebase Console**: Verificar configuració
4. **Play Console**: Revisar informes d'errors

---

**Document generat**: $(date)  
**Versió del document**: 1.0  
**Última actualització**: Octubre 2025