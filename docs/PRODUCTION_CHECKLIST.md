# 🚀 CHECKLIST PRODUCCIÓ - VALIDACIÓ FACTURES

## ⚠️ CONFIGURACIONS TEMPORALS PER TESTING

### 🛡️ PLAY INTEGRITY - **ACTUALMENT DESHABILITAT**
```bash
PLAY_INTEGRITY_ENABLED=false  # ⚠️ CANVIAR A true EN PRODUCCIÓ
```

**📍 Ubicació:** Fly.io Secrets (`fly secrets list --app validacio-backend`)

**🎯 Acció requerida abans de producció:**
```bash
# Activar Play Integrity per seguretat en producció
fly secrets set PLAY_INTEGRITY_ENABLED=true --app validacio-backend

# Configurar tokens vàlids (es necessitaran tokens reals de l'app en producció)
fly secrets set PLAY_INTEGRITY_TOKENS="token1,token2,token3" --app validacio-backend
```

## 📱 INDICADORS VISUALS ACTIUS

### 🧡 Alerta a Settings Screen (Android)
- **Què mostra:** "⚠️ MODE TEST ACTIU"
- **On:** Settings > Card taronja
- **Missatge:** "Play Integrity deshabilitat per testing. Recordar activar en producció!"

### 📝 Que cal fer abans de producció:
1. **Activar Play Integrity** a Fly.io
2. **Eliminar o comentar** la card d'alerta a `SettingsScreen.kt`
3. **Verificar** que tot funciona amb Play Integrity activat
4. **Documentar** els tokens vàlids utilitzats

## 🔒 CONFIGURACIONS DE SEGURETAT FINALS

### ✅ ABANS DE ANAR A PRODUCCIÓ REAL:

- [ ] `PLAY_INTEGRITY_ENABLED=true`
- [ ] `PLAY_INTEGRITY_TOKENS` configurats correctament
- [ ] Testing complet amb Play Integrity activat
- [ ] Eliminar alerta visual de "MODE TEST"
- [ ] Verificar que dispositius legítims no són rebutjats
- [ ] Documentar procediment per afegir nous tokens

## 📊 HISTORIAL DE CANVIS

### 🗓️ 15 Octubre 2025
- **Acció:** Deshabilitat Play Integrity per testing
- **Motiu:** Tokens rebutjats impedien upload d'albarans
- **Resultat:** App funciona completament, testing possible
- **Pendent:** Reactivar abans de producció real

## 🚨 RECORDATORIS IMPORTANTS

### ⚡ URGENT ABANS DE PRODUCCIÓ:
1. **NO oblidar reactivar Play Integrity**
2. **Eliminar indicador visual de test**
3. **Provar amb dispositius reals**
4. **Documentar tokens utilitzats**

### 📞 CONTACTE SI CAL AJUDA:
- Documentació Play Integrity: https://developer.android.com/google/play/integrity
- Firebase Console: https://console.firebase.google.com/
- Fly.io Dashboard: https://fly.io/dashboard

---
**⚠️ Aquest document serveix com a recordatori per no oblidar configuracions temporals!**