# 🛡️ Sistema de Seguretat Multi-Capa - Validació Factures

## 📋 **Resum Executiu**

Aquest document descriu l'arquitectura completa del sistema de seguretat implementat per l'aplicació **Validació Factures**, que inclou:

- **Control de Versions** automàtic
- **Autorització de Dispositius** amb workflow d'aprovació
- **Play Integrity API** per verificació d'autenticitat de l'app
- **Autenticació JWT** per usuaris
- **Sistema de Retry** automàtic per tokens caducats

---

## 🏗️ **Arquitectura del Sistema**

### **Backend (Spring Boot) - 4 Filtres en Cascada**

```
📥 REQUEST
    ↓
🛡️ PlayIntegrityFilter (@Order 1)
    ↓ [PASS]
🔒 DeviceAuthorizationFilter (@Order 2)  
    ↓ [PASS]
📱 VersionCheckFilter (@Order 3)
    ↓ [PASS]
🔑 JwtFilter (@Order 4)
    ↓ [PASS]
🎯 CONTROLLER
```

### **Android (Kotlin + Compose)**

```
📱 APP START
    ↓
🔧 MainActivity.onCreate()
    ↓
⚙️ IntegrityTokenProvider.init()
    ↓
🌐 ApiClient amb Interceptors
    ↓
📡 HTTP REQUESTS
```

---

## 🔄 **Flux Complet d'Aplicació: Inici → Login**

### **1. 🚀 Arrancar Aplicació**

#### **1.1 MainActivity.onCreate()**
```kotlin
// Ubicació: android/app/src/main/java/.../MainActivity.kt
lifecycleScope.launch {
    try {
        // 📡 Obtenir configuració del servidor
        val config = ApiClient.configService.getAppConfig()
        
        // 🎯 Configurar Play Integrity
        IntegrityTokenProvider.updateCloudProject(config.playIntegrityCloudProjectNumber)
        
        // 🔄 Obtenir token inicial
        IntegrityTokenProvider.getOrFetch(this@MainActivity, force = false)
        
        // ✅ Verificar versió mínima
        val minVersionCode = config.minSupportedVersion.filter { it.isDigit() }.toIntOrNull()
        if (minVersionCode != null && BuildConfig.VERSION_CODE < minVersionCode) {
            // 🚨 Forçar actualització
            // ... codi d'actualització
        }
    } catch (_: Exception) {
        // 🛡️ Ignorar errors de configuració inicial
    }
}
```

#### **1.2 Configuració de xarxa**
```kotlin
// 🌐 ApiClient inicialitzat amb:
private val headerInterceptor = Interceptor { chain ->
    val builder = chain.request().newBuilder()
        .addHeader("X-App-Version", BuildConfig.VERSION_CODE.toString())        // Versió
        .addHeader("X-Client-Platform", "ANDROID")                              // Plataforma
    
    // 🔥 Firebase Installation ID
    FidProvider.fid?.let { fid ->
        builder.addHeader("X-Firebase-Installation-Id", fid)
    }
    
    // 🛡️ Play Integrity Token
    IntegrityTokenProvider.currentToken?.let { token ->
        builder.addHeader("X-Play-Integrity-Token", token)
    }
    
    chain.proceed(builder.build())
}
```

### **2. 📡 Primera Petició: AppConfig**

#### **2.1 Request Android → Backend**
```
GET https://validacio-backend.fly.dev/config/app
Headers:
  X-App-Version: 9
  X-Client-Platform: ANDROID
  X-Firebase-Installation-Id: [FID]
  X-Play-Integrity-Token: dummy-token-for-testing
```

#### **2.2 Backend Processing**
```java
// 🛡️ PlayIntegrityFilter (@Order 1)
- ✅ /config està a EXCLUDED_PATHS → PASSA

// 🔒 DeviceAuthorizationFilter (@Order 2)  
- ✅ /config està a EXCLUDED_PATHS → PASSA

// 📱 VersionCheckFilter (@Order 3)
- ✅ No requereix validació per /config → PASSA

// 🔑 JwtFilter (@Order 4)
- ✅ /config és públic → PASSA

// 🎯 AppConfigController
return {
  "minSupportedVersion": "1.1.7",
  "message": "Cal actualitzar...",
  "updateUrl": "https://play.google.com/...",
  "playIntegrityCloudProjectNumber": 1013719707047
}
```

### **3. 🔑 Login d'Usuari**

#### **3.1 Usuario introdueix credencials**
```kotlin
// 📱 LoginPage.kt
onLoginClick = { username, password ->
    viewModel.login(username, password)
}
```

#### **3.2 Request de Login**
```
POST https://validacio-backend.fly.dev/api/auth/login
Headers:
  X-App-Version: 9
  X-Client-Platform: ANDROID  
  X-Firebase-Installation-Id: [FID]
  X-Play-Integrity-Token: dummy-token-for-testing
Body:
{
  "username": "usuari@exemple.com",
  "password": "contrasenya123"
}
```

#### **3.3 Backend Validation**
```java
// 🛡️ PlayIntegrityFilter (@Order 1)
- ✅ /api/auth està a EXCLUDED_PATHS → PASSA

// 🔒 DeviceAuthorizationFilter (@Order 2)
- ✅ /api/auth està a EXCLUDED_PATHS → PASSA  

// 📱 VersionCheckFilter (@Order 3)
- ✅ Versió 9 ≥ 1.1.7 → PASSA

// 🔑 JwtFilter (@Order 4)
- ✅ /api/auth és login endpoint → PASSA

// 🎯 AuthController.login()
- Validar credencials
- Generar JWT
- Retornar token
```

### **4. 🔒 Post-Login: Registre de Dispositiu**

#### **4.1 Registre automàtic de dispositiu**
```kotlin
// Després del login exitós
deviceRepository.registerDevice()
```

#### **4.2 Request de Registre**
```
POST https://validacio-backend.fly.dev/api/devices/register
Headers:
  X-App-Version: 9
  X-Client-Platform: ANDROID
  X-Firebase-Installation-Id: [FID] 
  X-Play-Integrity-Token: dummy-token-for-testing
Body:
{
  "deviceId": "[ANDROID_ID]",
  "model": "Pixel 6",
  "brand": "Google",
  "osVersion": "13"
}
```

#### **4.3 Backend Device Registration**
```java
// 🛡️ PlayIntegrityFilter (@Order 1)  
- ✅ /api/devices/register està a EXCLUDED_PATHS → PASSA

// 🔒 DeviceAuthorizationFilter (@Order 2)
- ✅ /api/devices/register està a EXCLUDED_PATHS → PASSA

// 📱 VersionCheckFilter (@Order 3)
- ✅ Versió vàlida → PASSA

// 🔑 JwtFilter (@Order 4)
- ✅ No requereix JWT per registre → PASSA

// 🎯 DeviceRegistrationController
- Crear dispositiu amb status PENDING
- Retornar deviceId
```

### **5. 🔗 Associació Usuari-Dispositiu**

#### **5.1 Request d'Associació**
```
POST https://validacio-backend.fly.dev/api/devices/associate-user
Headers:
  Authorization: Bearer [JWT_TOKEN]
  X-App-Version: 9
  X-Client-Platform: ANDROID
  X-Firebase-Installation-Id: [FID]
  X-Play-Integrity-Token: dummy-token-for-testing
Body:
{
  "deviceId": "[DEVICE_ID]"
}
```

#### **5.2 Backend Association**
```java
// 🛡️ PlayIntegrityFilter (@Order 1)
- ✅ /api/devices/associate-user està a EXCLUDED_PATHS → PASSA

// 🔒 DeviceAuthorizationFilter (@Order 2)  
- ✅ /api/devices/associate-user està a EXCLUDED_PATHS → PASSA

// 📱 VersionCheckFilter (@Order 3)
- ✅ Versió vàlida → PASSA

// 🔑 JwtFilter (@Order 4)
- ✅ JWT vàlid → EXTREU USER_ID

// 🎯 DeviceRegistrationController.associateUserToDevice()
- Associar user_id amb device_id
- Retornar success
```

### **6. 📊 Requests Protegits (ex: Llistar Factures)**

#### **6.1 Request Protegit**
```
GET https://validacio-backend.fly.dev/api/factures
Headers:
  Authorization: Bearer [JWT_TOKEN]
  X-App-Version: 9
  X-Client-Platform: ANDROID
  X-Firebase-Installation-Id: [FID]
  X-Play-Integrity-Token: dummy-token-for-testing
```

#### **6.2 Backend Full Security Chain**
```java
// 🛡️ PlayIntegrityFilter (@Order 1)
- 🔍 Validar X-Play-Integrity-Token contra PLAY_INTEGRITY_TOKENS
- ✅ "dummy-token-for-testing" és vàlid → PASSA

// 🔒 DeviceAuthorizationFilter (@Order 2)
- 🔍 Verificar device amb Firebase-Installation-Id està APPROVED
- ✅ Dispositiu autoritzat → PASSA

// 📱 VersionCheckFilter (@Order 3)  
- 🔍 Verificar X-App-Version ≥ minSupportedVersion
- ✅ Versió 9 ≥ 1.1.7 → PASSA

// 🔑 JwtFilter (@Order 4)
- 🔍 Validar JWT i extreure user_id
- ✅ JWT vàlid → PASSAR amb user context

// 🎯 FacturaController.getFactures()
- Retornar factures de l'usuari
```

---

## 🛡️ **Sistema de Retry Automàtic**

### **Escenari: Token Play Integrity Caducat**

```kotlin
// PlayIntegrityRetryInterceptor.kt
override fun intercept(chain: Interceptor.Chain): Response {
    val originalRequest = chain.request()
    
    // 1️⃣ Primer intent
    var response = chain.proceed(originalRequest)
    
    // 2️⃣ Si falla amb Play Integrity error
    if (!response.isSuccessful && response.code == 401) {
        val responseBody = response.peekBody(2048).string()
        
        if (responseBody.contains("INVALID_PLAY_INTEGRITY_TOKEN", ignoreCase = true)) {
            response.close()
            
            // 3️⃣ Refresh token automàtic
            runBlocking {
                IntegrityTokenProvider.getOrFetch(context, force = true)
            }
            
            // 4️⃣ Retry amb nou token
            val newRequest = originalRequest.newBuilder()
            IntegrityTokenProvider.currentToken?.let { token ->
                newRequest.removeHeader("X-Play-Integrity-Token")
                newRequest.addHeader("X-Play-Integrity-Token", token)
            }
            
            response = chain.proceed(newRequest.build())
        }
    }
    
    return response
}
```

---

## ⚙️ **Configuració de Variables d'Entorn**

### **Backend (Fly.io)**
```bash
# Play Integrity
PLAY_INTEGRITY_ENABLED=true
PLAY_INTEGRITY_TOKENS="dummy-token-for-testing"
PLAY_INTEGRITY_CLOUD_PROJECT_NUMBER=1013719707047

# Control de Versions
MIN_SUPPORTED_VERSION="1.1.7"
APP_UPDATE_MESSAGE="Cal actualitzar l'aplicació per continuar"
APP_UPDATE_URL="https://play.google.com/store/apps/details?id=..."

# Debug (només development)
DEBUG_ENDPOINTS_ENABLED=false
```

### **Android (BuildConfig)**
```kotlin
// build.gradle.kts
buildConfigField("String", "BASE_URL", "\"https://validacio-backend.fly.dev/\"")
buildConfigField("int", "VERSION_CODE", "$releaseVersionCode")
```

---

## 🔄 **Estats de Dispositius**

| Estat | Descripció | Pot fer requests? |
|-------|------------|-------------------|
| `PENDING` | Dispositiu registrat, esperant aprovació | ❌ NO |
| `APPROVED` | Dispositiu aprovat per admin | ✅ SÍ |
| `REVOKED` | Dispositiu revocat per admin | ❌ NO |

---

## 🎯 **Endpoints i Exclusions**

### **EXCLUDED_PATHS (no necessiten Play Integrity)**
- `/api/auth/*` - Login/logout
- `/api/devices/register` - Registre inicial
- `/api/devices/associate-user` - Associació usuari
- `/config/*` - Configuració pública
- `/ping` - Health check

### **PROTECTED_PATHS (necessiten tots els filtres)**
- `/api/factures/*` - Gestió factures
- `/api/albarans/*` - Gestió albarans  
- `/api/ots/*` - Ordres de treball
- `/api/pressupostos/*` - Pressupostos
- `/api/usuaris/*` - Gestió usuaris (admins)

---

## 🚀 **Desplegament**

### **Backend**
```bash
cd backend/
mvn clean package -DskipTests
fly deploy
```

### **Android**  
```bash
cd android/
./gradlew assembleStagingDebug
# APK generat a: app/build/outputs/apk/staging/debug/
```

---

## 🐛 **Debugging**

### **Logs Backend**
```bash
fly logs -a validacio-backend
```

### **Logs Android**
```bash
adb logcat | grep -E "(PlayIntegrity|UPLOAD_ALBARA|API_CLIENT)"
```

### **Testing Manual**
```bash
# Test AppConfig
curl https://validacio-backend.fly.dev/config/app

# Test amb Play Integrity
curl -H "X-Play-Integrity-Token: dummy-token-for-testing" \
     -H "X-Client-Platform: ANDROID" \
     -H "X-App-Version: 9" \
     https://validacio-backend.fly.dev/api/devices
```

---

## ⚠️ **Consideracions de Producció**

### **🔒 Seguretat**
1. **Eliminar token dummy** de `IntegrityService.kt`
2. **Configurar tokens reals** a `PLAY_INTEGRITY_TOKENS`
3. **Deshabilitar logs verbosos** a ApiClient
4. **Eliminar DebugController** del backend

### **📱 Google Play**
1. **Pujar APK signat** a Play Console
2. **Verificar SHA-1** del certificat de producció
3. **Activar Play Integrity API** al projecte Firebase
4. **Provar amb dispositius reals**

### **🔧 Monitoratge**
1. **Logs de tokens rejections**
2. **Mètriques de retry automàtic**
3. **Alertes de dispositius revocats**
4. **Control de versions automàtic**

---

## 📞 **Contacte i Suport**

- **Desenvolupador**: Team Copilot
- **Repositori**: VALIDACIO-FACTURES
- **Versió App**: 1.1.9
- **Versió Backend**: Latest deployment

---

*Documentació generada: 9 Octubre 2025*