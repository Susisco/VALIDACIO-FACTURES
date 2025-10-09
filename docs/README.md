# 📁 Índex de Documentació - VALIDACIÓ FACTURES

Benvingut a la documentació completa del projecte **VALIDACIÓ FACTURES**. Aquesta pàgina serveix com a punt d'entrada per navegar per tota la documentació tècnica organitzada.

## 🗂️ **Estructura de Documentació**

### 🏗️ **[Arquitectura](arquitectura/)**
Documentació tècnica sobre el disseny i l'arquitectura del sistema.

- **Diagrames del sistema**: Visualitzacions de l'arquitectura completa
- **Especificacions tècniques**: Detalls d'implementació per component
- **Patterns utilitzats**: MVVM, Repository, Multi-layer Security

### 🛡️ **[Seguretat](seguretat/)**
Documentació completa del sistema de seguretat multi-capa.

- **[Sistema de Seguretat Completa](seguretat/SISTEMA_SEGURETAT_COMPLETA.md)**: Guia principal del sistema de seguretat
- **Play Integrity**: Configuració i implementació
- **JWT & Device Authorization**: Gestió d'autenticació i autorització
- **Headers de seguretat**: Documentació dels headers requerits

### 🚀 **[Desplegament](desplegament/)**
Guies paso a paso per al desplegament de cada component.

- **[Deploy a Fly.io](desplegament/DEPLOY_FLYIO.md)**: Backend Spring Boot
- **[Deploy a Vercel](desplegament/DEPLOY_VERCEL.md)**: Frontend React
- **Google Play Console**: Android App deployment
- **Docker Compose**: Entorn local i desenvolupament

### 📊 **[Resultats](resultats/)**
Reports, logs i documentació de resultats del projecte.

- **[Èxit APK/AAB Producció v1.2.0](resultats/ÈXIT_APK_PRODUCCIO_v1.2.0.md)**: Report del build exitós
- **[Commits GitHub Actualitzat](resultats/COMMITS_GITHUB_ACTUALITZAT.md)**: Històrial de commits organitzats
- **Performance reports**: Mètriques i anàlisis
- **Build logs**: Informes de builds i desplegaments

### 🤝 **[Contribució](contribucio/)**
Guies per a desenvolupadors que volen contribuir al projecte.

- **[Guia de Contribució](contribucio/CONTRIBUTING.md)**: Com contribuir al projecte
- **Coding standards**: Convencions de codi per cada tecnologia
- **Setup d'entorn**: Configuració de l'entorn de desenvolupament
- **Process de PR**: Workflow de Pull Requests

---

## 📚 **Documents Principals**

### 📋 **Projecte**
- **[README Principal](../README.md)**: Introducció i overview complet
- **[CHANGELOG](../CHANGELOG.md)**: Històrial de versions i canvis
- **[LICENSE](../LICENSE)**: Llicència del projecte

### 🔧 **Configuració**
- **[Docker Compose](../docker-compose.yml)**: Configuració de contenidors
- **[CI/CD Pipeline](../.github/workflows/ci-cd.yml)**: Automatització GitHub Actions

---

## 🎯 **Navegació Ràpida per Rol**

### 👨‍💻 **Desenvolupadors**
```
📖 Començar aquí:
├── 📋 README.md (visió general)
├── 🤝 contribucio/CONTRIBUTING.md (com contribuir)
├── 🏗️ arquitectura/ (entendre el sistema)
└── 🚀 desplegament/ (deploy local)
```

### 🔐 **DevOps/Seguretat**
```
🛡️ Focus en seguretat:
├── 🔐 seguretat/SISTEMA_SEGURETAT_COMPLETA.md
├── 🚀 desplegament/ (totes les guies)
├── 🔧 .github/workflows/ (CI/CD)
└── 📊 resultats/ (logs i reports)
```

### 📱 **Desenvolupadors Android**
```
📱 Específic Android:
├── 🏗️ arquitectura/ (arquitectura general)
├── 🔐 seguretat/ (Play Integrity, headers)
├── 📊 resultats/ÈXIT_APK_PRODUCCIO_v1.2.0.md
└── 🤝 contribucio/CONTRIBUTING.md (estàndards Android)
```

### 🌐 **Desenvolupadors Frontend**
```
🌐 Específic Frontend:
├── 🏗️ arquitectura/ (comunicació amb backend)
├── 🚀 desplegament/DEPLOY_VERCEL.md
├── 🤝 contribucio/CONTRIBUTING.md (estàndards React)
└── 🔧 configuració d'entorn local
```

### 🔧 **Desenvolupadors Backend**
```
🔧 Específic Backend:
├── 🔐 seguretat/ (filtres, JWT, Play Integrity)
├── 🚀 desplegament/DEPLOY_FLYIO.md
├── 🏗️ arquitectura/ (API design)
└── 🤝 contribucio/CONTRIBUTING.md (estàndards Spring Boot)
```

---

## 🔍 **Com Trobar el que Busques**

### **Per Funcionalitat**
- **Seguretat**: `seguretat/` + components específics
- **Desplegament**: `desplegament/` + cloud specific
- **Arquitectura**: `arquitectura/` + component design
- **Contribució**: `contribucio/` + coding standards

### **Per Component**
- **Android**: Buscar "📱" o "Android" a qualsevol document
- **Frontend**: Buscar "🌐" o "React" a qualsevol document  
- **Backend**: Buscar "🔧" o "Spring Boot" a qualsevol document

### **Per Tipus de Tasca**
- **Setup inicial**: README.md → contribucio/CONTRIBUTING.md
- **Deploy production**: desplegament/ specific guides
- **Debug problemes**: resultats/ + seguretat/
- **Afegir funcionalitat**: arquitectura/ + contribucio/

---

## 📞 **Necessites Ajuda?**

Si no trobes el que busques:

1. **🔍 Cerca** a la documentació amb Ctrl+F
2. **📋 Issues** a [GitHub Issues](https://github.com/Susisco/VALIDACIO-FACTURES/issues)
3. **📧 Email** susisco@ajterrassa.cat
4. **💬 Discussions** a GitHub Discussions

---

## 🔄 **Mantenir la Documentació**

Aquesta documentació és un document viu. Quan afegeixis nova funcionalitat:

1. **Actualitza** la documentació corresponent
2. **Afegeix** nous documents si cal
3. **Actualitza** aquest índex
4. **Verifica** que els links funcionin

---

<div align="center">

**📚 Documentació mantinguda per l'equip AJTERRASSA**

*Última actualització: 9 Octubre 2025*

</div>