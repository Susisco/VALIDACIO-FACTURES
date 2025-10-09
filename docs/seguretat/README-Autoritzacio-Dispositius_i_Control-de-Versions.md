# Autorització de Dispositius i Control de Versions (App Android)

Aquest document descriu l’arquitectura i els procediments per:
1) **Autoritzar dispositius** (whitelist basada en FID) i credencials.  
2) **Forçar versió mínima** a client i servidor.  
3) **Validar integritat** amb Google Play Integrity.  
4) **Operar i monitorar** via panell d’administració i mètriques d’adopció.

Context: projecte **Validació de Factures – Enviament d’Albarans** (ús corporatiu).

---

## 1) Objectius i Principis

- **Només** poden operar **dispositius autoritzats** i **usuaris autenticats**.  
- Tota la flota corre **versions suportades**; versions antigues queden **bloquejades**.  
- El backend valida **autenticitat** d’app i dispositiu (**Play Integrity**).  
- L’equip d’operacions té **visibilitat i control** (aprovar/revocar, forçar updates).

---

## 2) Arquitectura (visió ràpida)

**Client Android**
- Envia **FID** (Firebase Installations ID), **versionCode** i **Authorization** a cada crida (via **OkHttp Interceptor**).
- Llegeix `minSupportedVersion` i força **In-app Update (immediate)** si cal.
- Obté **token de Play Integrity** (amb `nonce`) per a crides crítiques.

**Backend**
- **Whitelist** de dispositius (`device_registrations`).
- **Tall per versió mínima** (refusa `X-App-Version` inferior).
- **Validació Play Integrity** (verifica JWS, `nonce` i veredictes).
- **Panell d’admin** i **API d’operació** (aprovar/revocar FIDs, veure versions).

---

## 3) Fluxos clau

### 3.1 Alta i autorització de dispositiu
1. L’app obté el **FID** i l’envia al backend → estat **PENDING**.  
2. L’admin revisa i **APPROVE** el FID (o auto-approve segons política).  
3. A partir d’aquí, el backend només accepta crides amb **FID aprovat**.

### 3.2 Control de versions
1. El backend exposa `GET /config/app` → `{ minSupportedVersion, message, updateUrl }`.  
2. El client compara `versionCode` i, si és inferior: **In-app Update (immediate)**.  
3. El backend **refusa** peticions amb versió inferior (p. ex. `426 Upgrade Required`).

### 3.3 Verificació d’integritat (Play Integrity)
1. El client demana token d’**Integrity** a Google (inclou `nonce`).  
2. Envia el token al backend.  
3. El backend **verifica signatura** i **valida veredictes** (app i dispositiu).  
4. Si no compleix la política → **bloqueig** i registre del motiu.

---

## 4) Contractes de capa (headers i normes)

### 4.1 Headers obligatoris de cada crida
```
Authorization: Bearer <JWT>
X-App-Version: <versionCode integer>
X-Device-FID: <firebaseInstallationsId>
X-Integrity-Token: <JWS opcional/segons endpoint>
```

### 4.2 Respostes de control de versions
- **426 Upgrade Required** + body amb missatge i `updateUrl`.  
- **401/403** per credencials o dispositiu no autoritzat.

---

## 5) Endpoints principals (backend)

- `GET /config/app` → `{ minSupportedVersion, message, updateUrl }`  
- `POST /devices/register` → registra FID (crea **PENDING** si no existeix)  
- `GET /admin/devices` → llista (filters: status, version, user, lastSeenAt)  
- `POST /admin/devices/{fid}/approve` · `POST /admin/devices/{fid}/revoke`  
- `GET /admin/versions/summary` → agregats `{ versionCode → count }`

> **Nota:** Els endpoints d’admin requereixen rol **ADMIN** i s’auditen.

---

## 6) Model de dades (mínim)

**`device_registrations`**
- `fid` (PK), `userId`, `status` enum: `PENDING|APPROVED|REVOKED`  
- `appVersion` (darrera vista), `createdAt`, `lastSeenAt`

**`integrity_logs`** (opcional, o a logs)
- `fid`, `timestamp`, `verdict` (resum), `reason/detail`

---

## 7) Política d’integritat

- Accepta només `appIntegrity = PLAY_RECOGNIZED` i `deviceIntegrity = MEETS_DEVICE_INTEGRITY` (ajusta segons risc).  
- **Caducitat**: el token és vàlid per minuts; no cachegis més del necessari.  
- **Nonce**: sempre únic per sol·licitud (prevé replays).  
- En error de xarxa: **retry** controlat; en veredicte negatiu: **bloqueig**.

---

## 8) Operació i panell d’admin

**UI (React + Mantine)**
- **Dispositius**: taula (FID, usuari, estat, versió, `lastSeenAt`) amb **Aprovar/Revocar**.  
- **Versions**: gràfic d’adopció per `versionCode` + taula resum.  
- **Configuració**: editar `minSupportedVersion` i missatge d’avís (desa a backend).  
- **Seguretat**: rutes d’admin amb rol, audit trail d’accions.

**Procediment de llançament**
- _internal_ → _closed_ → **% gradual** → 100%  
- Monitoritza **Play Console** (adopció) i **Crashlytics** (errors per versió).  
- Si hi ha incident crític: puja `minSupportedVersion` (força update) i, si cal, **REVOKE** FIDs.

---

## 9) Bones pràctiques i anti-patrons

- ✅ Mantén el **tall d’API** al servidor (no només UI al client).  
- ✅ Usa **FID** (ID d’instal·lació) en lloc d’IDs de maquinari (privacitat i estabilitat).  
- ✅ Guarda motius de bloqueig d’integritat per diagnòstic.  
- ❌ No distribuir APKs fora de Play/EMM.  
- ❌ No confiar només en `ANDROID_ID` per llicenciar.  
- ❌ No obviar monitoratge d’adopció (vas “a cegues”).

---

## 10) Checklist ràpid

- [ ] `minSupportedVersion` actiu + **rebuig** d’APIs submínimes.  
- [ ] **OkHttp Interceptor** afegeix `Authorization`, `X-App-Version`, `X-Device-FID`, `X-Integrity-Token`.  
- [ ] **Play Integrity** validada **server-side** (signatura, `nonce`, veredictes).  
- [ ] **Panell d’admin** amb Aprovar/Revocar i estadístiques de versions.  
- [ ] **Proves**: app antiga → 426; FID no autoritzat → 403; Integrity KO → 403/422; token caducat → retry.  
- [ ] **Logs/Auditoria**: accions d’admin i motius de bloqueig.

---

## 11) Futur/Extensió

- **Android Enterprise + EMM** (Intune/Workspace ONE): app privada, auto-updates d’alta prioritat, revocació remota.  
- **Rate limiting** i alertes SIEM per intents repetits amb Integrity invàlid.  
- **Versionat d’API** (headers) per migracions trenacomp.  
- **Managed App Config** (si EMM) per injectar `BASE_URL`/entorns.

---

## 12) Compliment (GDPR)

- Dades mínimes: FID i metadades d’ús vinculades a usuari.  
- No emmagatzemar identificadors de maquinari.  
- Política de privacitat: finalitat (seguretat/llicència), conservació, drets d’accés.

---

## 13) Glossari

- **FID**: Firebase Installations ID (ID d’instal·lació per app).  
- **versionCode**: enter que identifica la versió de binari Android.  
- **Play Integrity**: servei de Google que atesta integritat d’app/dispositiu.  
- **Whitelist**: llista de dispositius autoritzats a operar.

---

## 14) Contacte/Responsabilitats

- **Producte / Seguretat**: criteri de política d’integritat i versions mínimes.  
- **Backend**: validació d’headers, tall d’API, endpoints d’admin.  
- **Mobile**: Interceptor, In-app Updates, flux d’Integrity.  
- **Operacions**: revisió d’adopció, aprovacions/revocacions, resposta a incidents.

--- 

> **En resum**: defensa en profunditat amb **whitelist + credencials**, **versió mínima client/servidor**, **Play Integrity** i **panell d’operació**. Garantim que només **dispositius legítims i autoritzats**, en **versions suportades**, poden consumir l’API.
