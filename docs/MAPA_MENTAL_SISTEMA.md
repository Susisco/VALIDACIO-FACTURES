# 🧠 Mapa Mental - Sistema Validació Factures

## 🎯 **Vista General del Sistema**

```mermaid
graph TB
    subgraph "📱 ANDROID APP"
        A1[MainActivity] --> A2[IntegrityTokenProvider]
        A2 --> A3[ApiClient + Interceptors]
        A3 --> A4[HTTP Requests]
    end
    
    subgraph "🌐 NETWORK"
        N1[Internet] --> N2[Fly.io CDN]
        N2 --> N3[validacio-backend.fly.dev]
    end
    
    subgraph "🛡️ BACKEND SECURITY"
        B1[PlayIntegrityFilter] --> B2[DeviceAuthorizationFilter]
        B2 --> B3[VersionCheckFilter]  
        B3 --> B4[JwtFilter]
        B4 --> B5[Controllers]
    end
    
    subgraph "🗄️ DATABASE"
        D1[(PostgreSQL)] 
        D2[Usuaris] 
        D3[Dispositius]
        D4[Factures]
    end
    
    A4 --> N1
    N3 --> B1
    B5 --> D1
    D1 --> D2
    D1 --> D3
    D1 --> D4
```

---

## 🔄 **Flux d'Inici d'Aplicació**

```mermaid
sequenceDiagram
    participant U as 👤 Usuari
    participant A as 📱 Android App
    participant B as 🛡️ Backend
    participant D as 🗄️ Database
    participant G as 🔥 Google/Firebase

    Note over U,G: 🚀 INICI D'APLICACIÓ
    
    U->>A: Obre App
    A->>A: MainActivity.onCreate()
    A->>B: GET /config/app
    Note right of A: Headers:<br/>X-Client-Platform: ANDROID<br/>X-App-Version: 9
    B->>A: AppConfig + playIntegrityCloudProjectNumber
    A->>G: Configurar Play Integrity (CloudProject: 1013719707047)
    A->>A: IntegrityTokenProvider.getOrFetch()
    Note over A: Token: "dummy-token-for-testing"
    A->>A: ✅ App Ready
```

---

## 🔑 **Flux de Login**

```mermaid
sequenceDiagram
    participant U as 👤 Usuari  
    participant A as 📱 Android App
    participant PI as 🛡️ PlayIntegrityFilter
    participant DA as 🔒 DeviceAuthFilter
    participant VC as 📱 VersionCheckFilter
    participant JWT as 🔑 JwtFilter
    participant AC as 🎯 AuthController
    participant D as 🗄️ Database

    Note over U,D: 🔑 LOGIN PROCESS
    
    U->>A: Introdueix credencials
    A->>PI: POST /api/auth/login
    Note right of A: Headers:<br/>X-Play-Integrity-Token: dummy-token-for-testing<br/>X-Client-Platform: ANDROID<br/>X-App-Version: 9
    
    PI->>PI: ✅ /api/auth a EXCLUDED_PATHS
    PI->>DA: PASSA →
    DA->>DA: ✅ /api/auth a EXCLUDED_PATHS  
    DA->>VC: PASSA →
    VC->>VC: ✅ Versió 9 ≥ 1.1.7
    VC->>JWT: PASSA →
    JWT->>JWT: ✅ Login endpoint públic
    JWT->>AC: PASSA →
    
    AC->>D: Validar credencials
    D->>AC: ✅ Usuari vàlid
    AC->>AC: Generar JWT
    AC->>A: 200 OK + JWT Token
    
    A->>A: Guardar JWT
    Note over A: ✅ Login Exitós
```

---

## 🔒 **Flux de Registre de Dispositiu**

```mermaid
sequenceDiagram
    participant A as 📱 Android App
    participant B as 🛡️ Backend Filters
    participant DRC as 🎯 DeviceController
    participant D as 🗄️ Database
    participant ADMIN as 👨‍💼 Admin

    Note over A,ADMIN: 🔒 DEVICE REGISTRATION
    
    A->>B: POST /api/devices/register
    Note right of A: Body: {deviceId, model, brand, osVersion}
    B->>B: ✅ Tots els filtres PASSA (/register a EXCLUDED_PATHS)
    B->>DRC: Request arribat
    
    DRC->>D: INSERT INTO dispositius (status=PENDING)
    D->>DRC: ✅ Dispositiu creat
    DRC->>A: 200 OK + deviceId
    
    Note over D: 📋 Dispositiu amb status PENDING
    
    ADMIN->>D: Revisar dispositius pendents
    ADMIN->>D: UPDATE status = APPROVED/REVOKED
    
    Note over D: ✅ Dispositiu APROVAT/REVOCAT
```

---

## 🔗 **Flux d'Associació Usuari-Dispositiu**

```mermaid
sequenceDiagram
    participant A as 📱 Android App
    participant B as 🛡️ Backend Filters
    participant DRC as 🎯 DeviceController
    participant D as 🗄️ Database

    Note over A,D: 🔗 USER-DEVICE ASSOCIATION
    
    A->>B: POST /api/devices/associate-user
    Note right of A: Headers:<br/>Authorization: Bearer JWT<br/>X-Play-Integrity-Token: dummy...<br/>Body: {deviceId}
    
    B->>B: ✅ associate-user a EXCLUDED_PATHS
    B->>DRC: Request + user_id (del JWT)
    
    DRC->>D: UPDATE dispositius SET user_id = ? WHERE device_id = ?
    D->>DRC: ✅ Associació creada
    DRC->>A: 200 OK + {"status": "success"}
    
    Note over D: 🔗 Usuari associat amb dispositiu
```

---

## 📊 **Flux de Request Protegit**

```mermaid
sequenceDiagram
    participant A as 📱 Android App
    participant PI as 🛡️ PlayIntegrityFilter
    participant DA as 🔒 DeviceAuthFilter
    participant VC as 📱 VersionCheckFilter
    participant JWT as 🔑 JwtFilter
    participant FC as 🎯 FacturaController
    participant D as 🗄️ Database

    Note over A,D: 📊 REQUEST PROTEGIT
    
    A->>PI: GET /api/factures
    Note right of A: Headers COMPLETS amb tots els tokens
    
    PI->>PI: 🔍 Validar X-Play-Integrity-Token
    Note over PI: dummy-token-for-testing ∈ PLAY_INTEGRITY_TOKENS?
    PI->>PI: ✅ Token vàlid
    PI->>DA: PASSA →
    
    DA->>D: SELECT * FROM dispositius WHERE firebase_id = ?
    D->>DA: status = APPROVED
    DA->>DA: ✅ Dispositiu autoritzat
    DA->>VC: PASSA →
    
    VC->>VC: 🔍 X-App-Version ≥ minSupportedVersion?
    VC->>VC: ✅ 9 ≥ 1.1.7
    VC->>JWT: PASSA →
    
    JWT->>JWT: 🔍 Validar JWT i extreure user_id
    JWT->>JWT: ✅ JWT vàlid + user_id
    JWT->>FC: PASSA → (amb user context)
    
    FC->>D: SELECT * FROM factures WHERE user_id = ?
    D->>FC: Llista de factures
    FC->>A: 200 OK + JSON factures
    
    Note over A: ✅ Dades rebudes
```

---

## 🔄 **Sistema de Retry Automàtic**

```mermaid
sequenceDiagram
    participant A as 📱 App
    participant RI as 🔄 RetryInterceptor
    participant ITP as 🛡️ IntegrityTokenProvider
    participant B as 🛡️ Backend
    participant G as 🔥 Google Play

    Note over A,G: 🔄 TOKEN CADUCAT - RETRY AUTOMÀTIC
    
    A->>RI: HTTP Request
    RI->>B: Request amb token vell
    B->>RI: 401 "INVALID_PLAY_INTEGRITY_TOKEN"
    
    RI->>RI: 🔍 Detectar error Play Integrity
    RI->>ITP: getOrFetch(force=true)
    ITP->>G: IntegrityManager.requestToken()
    G->>ITP: Nou token real
    ITP->>RI: ✅ Token actualitzat
    
    RI->>RI: Crear nova request amb token nou
    RI->>B: RETRY amb nou token
    B->>RI: 200 OK ✅
    RI->>A: Resposta exitosa
    
    Note over A: ✅ Retry transparent per l'usuari
```

---

## 🎯 **Mapa d'Endpoints i Seguretat**

```mermaid
graph TB
    subgraph "🟢 PÚBLICS (EXCLUDED_PATHS)"
        E1[/config/app] 
        E2[/ping]
        E3[/api/auth/login]
        E4[/api/devices/register]
        E5[/api/devices/associate-user]
    end
    
    subgraph "🔴 PROTEGITS (4 FILTRES)"
        P1[/api/factures/*]
        P2[/api/albarans/*]
        P3[/api/ots/*]
        P4[/api/pressupostos/*]
        P5[/api/usuaris/*]
    end
    
    subgraph "🛡️ FILTRES DE SEGURETAT"
        F1[PlayIntegrityFilter @Order1]
        F2[DeviceAuthFilter @Order2]
        F3[VersionCheckFilter @Order3]
        F4[JwtFilter @Order4]
    end
    
    E1 --> OK1[✅ Directe]
    E2 --> OK2[✅ Directe]
    E3 --> OK3[✅ Directe]
    E4 --> OK4[✅ Directe]
    E5 --> OK5[✅ Directe]
    
    P1 --> F1
    P2 --> F1
    P3 --> F1
    P4 --> F1
    P5 --> F1
    
    F1 --> F2
    F2 --> F3
    F3 --> F4
    F4 --> OK[✅ Controller]
```

---

## 📱 **Estats de Dispositius**

```mermaid
stateDiagram-v2
    [*] --> Registrat: POST /devices/register
    Registrat --> PENDING: Status inicial
    
    PENDING --> APPROVED: Admin aprova
    PENDING --> REVOKED: Admin rebutja
    
    APPROVED --> REVOKED: Admin revoca
    APPROVED --> Active: Pot fer requests
    
    REVOKED --> [*]: Dispositiu bloquejat
    Active --> REVOKED: Comportament sospitós
    
    note right of PENDING: ❌ No pot fer requests protegits
    note right of APPROVED: ✅ Pot fer requests protegits  
    note right of REVOKED: ❌ Dispositiu bloquejat permanentment
```

---

## ⚙️ **Variables de Configuració**

```mermaid
mindmap
  root)🔧 CONFIGURACIÓ(
    📱 Android
      BuildConfig
        BASE_URL
        VERSION_CODE
        DEBUG
      Firebase
        Project: 1013719707047
        SHA1: configurada
    🛡️ Backend
      Play Integrity
        ENABLED: true
        TOKENS: dummy-token-for-testing
        PROJECT: 1013719707047
      Versions
        MIN_SUPPORTED: 1.1.7
        UPDATE_MSG: Cal actualitzar...
        UPDATE_URL: Google Play
      Debug
        ENDPOINTS_ENABLED: false
    🗄️ Database
      Usuaris
        id, username, password_hash
      Dispositius
        id, device_id, firebase_id, status
        user_id (FK), created_at
      Factures
        id, referencia, import_total
        user_id (FK), proveidor_id
```

---

## 🚀 **Procediments de Deploy**

```mermaid
gitgraph
    commit id: "Init"
    branch android-updates
    commit id: "v1.1.8"
    commit id: "Play Integrity"
    commit id: "v1.1.9"
    checkout main
    merge android-updates
    
    branch backend-security  
    commit id: "4-layer filters"
    commit id: "PlayIntegrity API"
    commit id: "Device Authorization"
    checkout main
    merge backend-security
    
    branch production-ready
    commit id: "Remove dummy tokens"
    commit id: "Real Play Integrity"
    commit id: "Production deploy"
```

---

## 📊 **Mètriques i Monitoratge**

```mermaid
graph LR
    subgraph "📊 MÈTRIQUES"
        M1[Requests/minut]
        M2[Tokens rejections]
        M3[Device registrations]
        M4[Version compliance]
        M5[Error rates]
    end
    
    subgraph "🚨 ALERTES"
        A1[Token failure spike]
        A2[Versió obsoleta]
        A3[Dispositiu sospitós]
        A4[JWT compromise]
    end
    
    subgraph "📈 DASHBOARDS"
        D1[Security Overview]
        D2[Device Status]
        D3[User Activity]
        D4[System Health]
    end
    
    M1 --> D1
    M2 --> A1
    M3 --> D2
    M4 --> A2
    M5 --> D4
```

---

## 🎯 **Resum dels Components Clau**

| Component | Responsabilitat | Estat |
|-----------|----------------|-------|
| **PlayIntegrityFilter** | Validar tokens Google Play | ✅ Implementat |
| **DeviceAuthorizationFilter** | Control dispositius aprovats | ✅ Implementat |
| **VersionCheckFilter** | Control versions mínimes | ✅ Implementat |
| **JwtFilter** | Autenticació usuaris | ✅ Implementat |
| **IntegrityTokenProvider** | Gestió tokens Android | ✅ Implementat |
| **PlayIntegrityRetryInterceptor** | Retry automàtic | ✅ Implementat |
| **AppConfig API** | Configuració dinàmica | ✅ Implementat |

---

*Mapa Mental generat: 9 Octubre 2025*  
*Versió Sistema: Android 1.1.9 + Backend Latest*