# 🤝 Guia de Contribució - VALIDACIÓ FACTURES

Gràcies per interessar-te en contribuir al projecte **VALIDACIÓ FACTURES**! Aquesta guia t'ajudarà a configurar l'entorn i seguir les millors pràctiques.

## 📋 **Taula de Continguts**

- [🚀 Configuració Ràpida](#-configuració-ràpida)
- [🏗️ Entorn de Desenvolupament](#️-entorn-de-desenvolupament)
- [📝 Estàndards de Codi](#-estàndards-de-codi)
- [🧪 Testing](#-testing)
- [📤 Process de Pull Request](#-process-de-pull-request)
- [🏷️ Versionat](#️-versionat)

---

## 🚀 **Configuració Ràpida**

### **1. Fork i Clone**
```bash
# Fork del repositori a GitHub
# Després clonar amb submòduls
git clone --recursive https://github.com/YOUR-USERNAME/VALIDACIO-FACTURES.git
cd VALIDACIO-FACTURES
```

### **2. Configurar upstream**
```bash
git remote add upstream https://github.com/Susisco/VALIDACIO-FACTURES.git
git fetch upstream
```

### **3. Crear branch de treball**
```bash
git checkout -b feature/nova-funcionalitat
# o
git checkout -b fix/correccio-bug
# o
git checkout -b docs/millora-documentacio
```

---

## 🏗️ **Entorn de Desenvolupament**

### **Prerequisits**
- **Docker** 20+ & **Docker Compose** 2+
- **Node.js** 18+ & **npm** 8+
- **Java** 17+ & **Maven** 3.8+
- **Android Studio** Flamingo+
- **Git** 2.40+

### **Configuració Backend**
```bash
cd backend

# Copiar configuració d'exemple
cp src/main/resources/application-dev.properties.example src/main/resources/application-dev.properties

# Editar amb les teves credencials
# NOTA: Mai committis credencials reals!

# Executar amb Docker
docker-compose up -d postgres
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### **Configuració Frontend**
```bash
cd frontend

# Instal·lar dependències
npm install

# Copiar configuració
cp .env.example .env.local

# Executar en mode desenvolupament
npm run dev
```

### **Configuració Android**
```bash
cd android

# Copiar configuració de keystore
cp keystore.properties.example keystore.properties

# Sincronitzar amb Android Studio
# Build en mode debug
./gradlew assembleDevDebug
```

---

## 📝 **Estàndards de Codi**

### **Commits Convencionals**
Utilitzem el format [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>[optional scope]: <description>

[optional body]

[optional footer(s)]
```

**Tipus de commits:**
- `feat`: Nova funcionalitat
- `fix`: Correcció de bug
- `docs`: Canvis en documentació
- `style`: Formatació de codi (sense canvis lògics)
- `refactor`: Refactorització de codi
- `test`: Afegir o modificar tests
- `chore`: Tasques de manteniment

**Exemples:**
```bash
feat(android): afegir sistema retry automàtic Play Integrity
fix(backend): corregir validació de tokens JWT
docs(readme): actualitzar guia d'instal·lació
style(frontend): aplicar formatting amb Prettier
```

### **Estàndards per Component**

#### **Backend (Java/Spring Boot)**
```java
// Nomenclatura de classes
@RestController
@RequestMapping("/api/v1/factures")
public class FacturaController {
    
    // Mètodes amb verbs HTTP clars
    @PostMapping
    public ResponseEntity<FacturaDto> crearFactura(@Valid @RequestBody CreateFacturaRequest request) {
        // Implementació
    }
}
```

#### **Frontend (TypeScript/React)**
```typescript
// Components amb PascalCase
export const FacturaForm: React.FC<FacturaFormProps> = ({ onSubmit }) => {
  // Hooks al principi
  const [loading, setLoading] = useState(false);
  
  // Handlers amb prefix handle
  const handleSubmit = useCallback((data: FacturaData) => {
    // Implementació
  }, []);
  
  return (
    <form onSubmit={handleSubmit}>
      {/* JSX */}
    </form>
  );
};
```

#### **Android (Kotlin)**
```kotlin
// Classes amb PascalCase
class FacturaRepository @Inject constructor(
    private val apiService: ApiService,
    private val localDb: FacturaDao
) {
    
    // Funcions amb camelCase
    suspend fun getFactures(): Flow<List<Factura>> {
        return flow {
            // Implementació
        }
    }
}
```

---

## 🧪 **Testing**

### **Backend Tests**
```bash
cd backend

# Executar tots els tests
./mvnw test

# Tests específics
./mvnw test -Dtest=FacturaControllerTest

# Coverage report
./mvnw jacoco:report
```

### **Frontend Tests**
```bash
cd frontend

# Unit tests amb Vitest
npm run test

# E2E tests amb Playwright
npm run test:e2e

# Coverage
npm run test:coverage
```

### **Android Tests**
```bash
cd android

# Unit tests
./gradlew testDevDebugUnitTest

# Instrumentation tests
./gradlew connectedDevDebugAndroidTest
```

### **Cobertura Mínima**
- Backend: 80%
- Frontend: 75%
- Android: 70%

---

## 📤 **Process de Pull Request**

### **1. Abans de crear PR**
```bash
# Sincronitzar amb upstream
git fetch upstream
git rebase upstream/main

# Executar tots els tests
# Backend
cd backend && ./mvnw test

# Frontend  
cd frontend && npm run test

# Android
cd android && ./gradlew test
```

### **2. Crear Pull Request**

**Títol del PR:**
```
feat(component): descripció clara de la funcionalitat
```

**Template del PR:**
```markdown
## 📝 Descripció
Descripció clara de què fa aquest PR.

## 🔄 Tipus de canvi
- [ ] Bug fix
- [ ] Nova funcionalitat
- [ ] Breaking change
- [ ] Documentació

## ✅ Checklist
- [ ] Tests escrits i passant
- [ ] Documentació actualitzada
- [ ] Codi revisat per auto-revisió
- [ ] No hi ha conflicts amb main

## 🧪 Com testejar
Passos per verificar els canvis:
1. Pas 1
2. Pas 2
3. Pas 3
```

### **3. Review Process**
- Mínim 1 aprovació requerida
- Tots els CI checks han de passar
- No conflicts amb la branca main
- Cobertura de testing mantinguda

---

## 🏷️ **Versionat**

Seguim [Semantic Versioning](https://semver.org/):

### **Format: MAJOR.MINOR.PATCH**

- **MAJOR**: Breaking changes
- **MINOR**: Noves funcionalitats (backward compatible)
- **PATCH**: Bug fixes (backward compatible)

### **Exemples**
- `1.0.0` → `1.0.1`: Bug fix
- `1.0.1` → `1.1.0`: Nova funcionalitat
- `1.1.0` → `2.0.0`: Breaking change

### **Release Process**
```bash
# 1. Crear tag
git tag -a v1.2.0 -m "Release v1.2.0: afegir sistema retry"

# 2. Push tag
git push upstream v1.2.0

# 3. GitHub Actions crearà el release automàticament
```

---

## 📋 **Issues i Feature Requests**

### **Reportar Bug**
```markdown
**Descripció del bug**
Descripció clara del problema.

**Passos per reproduir**
1. Anar a '...'
2. Clicar a '....'
3. Veure error

**Comportament esperat**
Què hauria de passar.

**Screenshots**
Si aplica, afegir captures.

**Entorn**
- OS: [e.g. Windows 11]
- Browser [e.g. Chrome 118]
- Versió [e.g. 1.2.0]
```

### **Feature Request**
```markdown
**És el teu feature request relacionat amb un problema?**
Descripció clara del problema.

**Solució proposada**
Què voldries que passés.

**Alternatives considerades**
Altres solucions que has considerat.

**Context addicional**
Altra informació útil.
```

---

## 📞 **Suport i Contacte**

- 💬 **Discussions**: [GitHub Discussions](https://github.com/Susisco/VALIDACIO-FACTURES/discussions)
- 🐛 **Issues**: [GitHub Issues](https://github.com/Susisco/VALIDACIO-FACTURES/issues)
- 📧 **Email**: susisco@ajterrassa.cat

---

## 📄 **Llicència**

En contribuir a aquest projecte, acceptes que les teves contribucions es licenciïn sota la mateixa llicència MIT del projecte.

---

**Gràcies per contribuir! 🎉**