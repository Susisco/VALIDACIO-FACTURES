# Gestió de dispositius (Android)

Aquest document descriu el disseny i funcionament de la gestió de dispositius mòbils per a l’app d’Albarans/Factures.

## Identitat de dispositiu
- Header obligatori: `X-Firebase-Installation-Id` (FID).
- Headers informatius: `X-App-Version`, `X-Client-Platform`.
- El FID identifica de forma única el dispositiu. No dupliquem registres pel mateix FID.

## Taula i camps principals
- `DeviceRegistration` (taula única):
  - `fid` (únic), `status` (PENDING, APPROVED, REVOKED, ARCHIVED, DELETED)
  - `appVersion`, `userId` (opcional)
  - `createdAt` (alta), `lastSeenAt` (última activitat)
  - `archivedAt`, `deletedAt` (marques opcionals per baixa lògica)

## Estats i comportament
- `PENDING`: Alta inicial. L’usuari no pot fer login fins que un administrador l’aprovi.
- `APPROVED`: Operatiu. Pot fer login i utilitzar l’app.
- `REVOKED`: Bloquejat manualment per l’administrador.
- `ARCHIVED`: Inactiu per inactivitat prolongada. No pot fer login. Es pot reactivar a APPROVED sense crear un nou registre.
- `DELETED`: Baixa lògica (conservem com a històric). Normalment no es reactivarà.

## Fluxos bàsics
1) Instal·lació/Primera obertura
   - L’app truca a `GET /api/devices/status` amb el FID.
   - Si no existeix, es crea en PENDING i es retorna l’estat i `registeredAt`.
2) Ús normal
   - Qualsevol crida amb FID actualitza `lastSeenAt` automàticament.
3) Desinstal·lació / Inactivitat
   - No hi ha esdeveniment directe. Un job diari arxiva dispositius sense activitat.
4) Re-activació
   - L’administrador pot reactivar (APPROVED) un dispositiu ARCHIVED o DELETED.

## Endpoints
- Públics (sense login):
  - `GET /api/devices/status` → retorna estat, `deviceId`, `deviceInfo`, `registeredAt`.
  - `POST /api/devices/register` → registra/actualitza (no canvia estats ARCHIVED/DELETED a PENDING).
- Admin (`ROLE_ADMINISTRADOR`):
  - `GET /api/admin/devices` → llistat de dispositius.
  - `POST /api/admin/devices/{fid}/approve` → passa a APPROVED.
  - `POST /api/admin/devices/{fid}/revoke` → passa a REVOKED.
  - `POST /api/admin/devices/{fid}/archive` → passa a ARCHIVED i marca `archivedAt`.
  - `POST /api/admin/devices/{fid}/delete-logical` → passa a DELETED i marca `deletedAt`.
  - `POST /api/admin/devices/{fid}/reactivate` → passa a APPROVED i neteja marques.

## Política de retenció i neteja
- Tasca programada diària (03:30) configurable per propietat `devices.cleanup.cron`.
- Retenció configurable a `application.yml`:
  - `devices.retention.archiveAfterDays` (per defecte 90) → si `lastSeenAt` és anterior, es canvia a ARCHIVED.
  - `devices.retention.deleteAfterDays` (per defecte 365) → si `archivedAt` és anterior, es canvia a DELETED.

## Consideracions de seguretat
- Per accedir a rutes protegides cal:
  - Dispositiu amb `status=APPROVED`.
  - Credencials d’usuari vàlides (JWT).
- El filtre de dispositius rebutja l’accés si l’estat no és APPROVED.

## Bones pràctiques
- No reactivar automàticament des de l’app un dispositiu ARCHIVED/DELETED.
- Fer servir l’email o el panell d’admin per reactivacions.
- Monitoritzar el nombre d’ARCHIVED/DELETED i ajustar retencions segons criteris de negoci.

## FAQ
- Què passa si l’usuari reinstal·la la app?
  - El FID normalment canvia → es crea un nou `DeviceRegistration` en PENDING.
- Es pot perdre el FID?
  - Sí, el sistema de Firebase el pot renovar. Per això tractem cada FID com un dispositiu nou.
- Per què no esborrem físicament?
  - Mantenim l’històric i la traçabilitat. `DELETED` és una baixa lògica.
