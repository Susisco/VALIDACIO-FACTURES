# 🛡️ Fix Play Integrity Production Issue

## 🔍 **Problema identificat**
L'app de producció està generant Play Integrity tokens que el backend rebutja.

## 📊 **Anàlisi dels logs**

### Android App
- ✅ JWT Token vàlid
- ✅ Retry automàtic funcionant
- ❌ Play Integrity tokens rebutjats

### Backend (Fly.io)
- ✅ Autenticació JWT correcta per `admin@admin.com`
- ❌ Tokens Play Integrity rebutjats:
  - Hash `d199a3db` → REBUTJAT
  - Hash `d81184cd` → REBUTJAT

## 🎯 **Causa arrel**
El **SHA-1 hash de producció** no està registrat a Firebase Console.

### Hash actual de producció:
```
SHA1: 31:94:3B:50:C1:9A:C4:E8:1A:BB:D3:76:AC:AE:DA:BC:A7:2D:24:90
```

## 🔧 **Solució**

### 1. Registrar hash SHA-1 a Firebase

1. **Firebase Console**: https://console.firebase.google.com/
2. **Projecte**: `1013719707047`
3. **Project Settings** ⚙️ → **Your apps** → Android app
4. **Afegir SHA certificate fingerprint**:
   ```
   31:94:3B:50:C1:9A:C4:E8:1A:BB:D3:76:AC:AE:DA:BC:A7:2D:24:90
   ```

### 2. Verificar configuració actual

Firebase Console → App Settings → comprovar quins hashes estan registrats.

### 3. Temps de propagació

Després d'afegir el hash, pot trigar fins a **30 minuts** per propagar-se.

## 📋 **Checklist de verificació**

- [ ] Hash SHA-1 afegit a Firebase Console
- [ ] Esperar 30 minuts per propagació
- [ ] Provar upload d'albarà amb foto
- [ ] Verificar logs del backend

## 🔄 **Procés de test**

1. Afegir hash a Firebase
2. Esperar propagació (30 min)
3. Provar funcionalitat:
   ```bash
   # Al dispositiu Android
   # 1. Obrir app
   # 2. Crear nou albarà
   # 3. Afegir foto
   # 4. Enviar
   ```

## 🎭 **Mock alternatiu (temporal)**

Si necessites una solució immediata, pots desactivar temporalment Play Integrity per aquesta funcionalitat:

### Backend - Temporal bypass (NO recomanat per producció)
```java
// PlayIntegrityFilter.java - NOMÉS PER TEST
if (request.getRequestURI().contains("/api/albarans/app/save-with-file")) {
    chain.doFilter(request, response);
    return;
}
```

**⚠️ IMPORTANT**: Aquesta solució és temporal i compromet la seguretat.

## 📱 **Informació del sistema**

- **Firebase Project**: `1013719707047`
- **App Version**: `15 (1.2.5)`
- **Backend Version**: `113`
- **Certificat Upload Key**: `upload-key.jks`
- **Release SHA-1**: `31:94:3B:50:C1:9A:C4:E8:1A:BB:D3:76:AC:AE:DA:BC:A7:2D:24:90`

---

**Prioritat**: 🔴 ALTA - L'app de producció no pot enviar albarans amb fotos.