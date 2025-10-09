# 📋 Changelog - VALIDACIÓ FACTURES

Tots els canvis notables d'aquest projecte es documentaran en aquest fitxer.

El format està basat en [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
i aquest projecte segueix [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Documentació professional completa amb estructura organitzada
- Guia de contribució detallada

---

## [1.2.0] - 2025-10-09 🎉

### Added
- **🔐 Sistema de seguretat multi-capa** complet implementat
- **🛡️ Play Integrity API** integració amb Firebase Project 1013719707047
- **🔄 Sistema retry automàtic** per gestió intel·ligent d'errors
- **📱 Android App Bundle (AAB)** generat per Google Play Console
- **📋 Documentació completa** del sistema de seguretat
- **🏗️ Reestructuració workspace** amb submòdul Android integrat

### Changed
- **📱 Versió Android** actualitzada de 1.1.9 a 1.2.0
- **🔑 Tokens dummy eliminats** del codi de producció
- **📊 Logging optimitzat** per producció (DEBUG vs NONE)
- **🔧 Configuració Firebase** per suportar tots els flavors (dev/staging/prod)

### Fixed
- **🐛 Gradle lock conflicts** resolts amb cleanup automàtic
- **🔧 Google Services configuration** per package names correctes
- **🔄 Headers de seguretat** complets en totes les requests
- **📱 Build staging/prod** variants funcionant correctament

### Security
- **🛡️ PlayIntegrityFilter** (@Order 1) - Validació integritat dispositiu
- **🔒 DeviceAuthorizationFilter** (@Order 2) - Control dispositius autoritzats  
- **📱 VersionCheckFilter** (@Order 3) - Verificació versió mínima
- **🔑 JwtFilter** (@Order 4) - Autenticació usuari JWT
- **🔄 PlayIntegrityRetryInterceptor** - Retry automàtic errors 401/403

---

## [1.1.9] - 2025-10-08

### Added
- Configuració inicial Play Integrity API
- Implementació bàsica de filtres de seguretat
- Documentació inicial del sistema

### Changed
- Millores en la configuració de desenvolupament
- Optimització de la comunicació client-servidor

---

## [1.1.0] - 2025-10-01

### Added
- **🌐 Frontend React** amb Vite i TypeScript
- **🔧 Backend Spring Boot** amb API REST completa
- **📱 Android App** amb Kotlin i Jetpack Compose
- **🐳 Docker Compose** per desenvolupament local
- **☁️ Desplegament cloud** (Fly.io + Vercel)

### Features
- Gestió completa de factures, albarans i pressupostos
- Sistema d'autenticació bàsic
- Interface web responsive
- App mòbil nativa Android

---

## [1.0.0] - 2025-09-15

### Added
- **📋 Projecte inicial** VALIDACIÓ FACTURES
- **🏗️ Arquitectura base** multi-component
- **📱 Prototip Android** funcional
- **🌐 Portal web** bàsic
- **🔧 API REST** inicial

### Infrastructure
- Configuració inicial de repositoris
- Setup de l'entorn de desenvolupament
- Documentació bàsica del projecte

---

## 📝 **Llegenda de Categories**

### Tipus de Canvis
- **Added** ✨ - Noves funcionalitats
- **Changed** 🔄 - Canvis en funcionalitats existents
- **Deprecated** ⚠️ - Funcionalitats que seran eliminades
- **Removed** ❌ - Funcionalitats eliminades
- **Fixed** 🐛 - Correccions de bugs
- **Security** 🔐 - Canvis relacionats amb seguretat

### Icones per Component
- 🔧 Backend (Spring Boot)
- 🌐 Frontend (React)
- 📱 Android (Kotlin)
- 📋 Documentació
- 🐳 Docker/DevOps
- ☁️ Cloud/Deploy
- 🧪 Testing
- 🔐 Seguretat

---

## 📋 **Com Mantenir aquest Changelog**

### Per cada release:

1. **Crear nova secció** amb número de versió i data
2. **Categoritzar canvis** segons tipus (Added, Changed, Fixed, etc.)
3. **Usar icones** per identificar components afectats
4. **Descripcions clares** de cada canvi
5. **Links a PRs/Issues** quan sigui relevant

### Exemple:
```markdown
## [1.3.0] - 2025-10-15

### Added
- 📱 **Nova funcionalitat X** en Android app ([#123](link-to-pr))
- 🔧 **Endpoint Y** al backend per suportar funcionalitat X

### Fixed  
- 🐛 **Bug Z** en la validació de formularis ([#124](link-to-issue))
```

---

**Mantingut per**: Equip AJTERRASSA  
**Última actualització**: 9 Octubre 2025