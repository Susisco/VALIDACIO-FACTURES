## 🔧 **Manual per Simular Dispositius**

Per tenir dispositius a la pantalla de gestió, pots registrar-los manualment seguint aquests passos:

### **Opció 1: Amb el navegador web** (Recomanada)

1. **Obre el navegador** i ves a l'**eina de desenvolupador** (F12)
2. **Obre la pestanya "Console"**
3. **Executa aquest codi** per simular un dispositiu Android:

```javascript
fetch('http://localhost:8080/api/devices/register', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'X-App-Version': '1.0.0'
  },
  body: JSON.stringify({
    fid: 'test-device-001'
  })
})
.then(response => response.text())
.then(data => console.log('Dispositiu registrat:', data));
```

4. **Prem Enter** per executar
5. **Actualitza la pantalla de dispositius** al frontend

### **Opció 2: Crear més dispositius de prova**

Pots crear diferents dispositius amb FIDs diferents:

```javascript
// Dispositiu 1
fetch('http://localhost:8080/api/devices/register', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'X-App-Version': '1.0.0'
  },
  body: JSON.stringify({ fid: 'android-phone-001' })
}).then(r => r.text()).then(console.log);

// Dispositiu 2 
fetch('http://localhost:8080/api/devices/register', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'X-App-Version': '1.1.0'
  },
  body: JSON.stringify({ fid: 'android-tablet-002' })
}).then(r => r.text()).then(console.log);

// Dispositiu 3
fetch('http://localhost:8080/api/devices/register', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'X-App-Version': '0.9.0'
  },
  body: JSON.stringify({ fid: 'android-watch-003' })
}).then(r => r.text()).then(console.log);
```

### **Opció 3: Amb eina externa (Postman/Insomnia)**

Si tens Postman o similar:

- **URL**: `http://localhost:8080/api/devices/register`
- **Mètode**: `POST`
- **Headers**:
  - `Content-Type: application/json`
  - `X-App-Version: 1.0.0`
- **Body (JSON)**:
```json
{
  "fid": "test-device-001"
}
```

---

## 📱 **Com funciona en la vida real**

Quan tinguis l'**app Android real**:

1. **L'app es baixa** del Play Store i s'instal·la
2. **Firebase Installation ID** es genera automàticament
3. **Primera connexió**: L'app crida `/api/devices/register` automàticament
4. **Dispositiu apareix** com `PENDING` a la pantalla d'admin
5. **Tu com a admin** el pots aprovar fent clic a "Aprova"
6. **Dispositiu aprovat** pot usar totes les funcions de l'API

---

## 🎯 **Resultat esperat**

Després de registrar dispositius, a la pantalla de **DISPOSITIUS** veuràs:

**Taula DISPOSITIUS:**
| FID | Estat | Accions |
|-----|--------|---------|
| test-device-001 | PENDING | [Aprova] [Revoca] |
| android-phone-001 | PENDING | [Aprova] [Revoca] |

**Taula VERSIONS D'APLICACIÓ:**
| Versió | Dispositius |
|--------|-------------|
| 1.0.0  | 1 |
| 1.1.0  | 1 |
| 0.9.0  | 1 |

Llavors pots **aprovar els dispositius** fent clic a "Aprova" i veure com canvia l'estat a `APPROVED`.