# Deploy Frontend React a Vercel

## Resumen de pasos para hacer deploy del frontend

### 🏗️ Estado actual del proyecto

**Projecte connectat a GitHub amb deploy automàtic activat:**
- **Repositori**: `Susisco/VALIDACIO-FACTURES`
- **Projecte Vercel**: `frontend-fund` 
- **Domini**: https://validacio-factures.vercel.app
- **Branca de producció**: `main`
- **Root Directory**: `frontend/` (projecte multi-directori)

**Cada `git push` a la branca `main` desplega automàticament** ✅

---

## 🚀 Mètodes de desplegament

### Mètode 1: Deploy automàtic via GitHub (ACTUAL) ⭐
**El mètode que utilitzem actualment**

```bash
# 1. Fer canvis al codi
# 2. Commit i push
git add .
git commit -m "Descripció dels canvis"
git push

# 3. Vercel desplega automàticament en ~2-3 minuts
# 4. Verificar a: https://validacio-factures.vercel.app
```

### Mètode 2: Deploy manual via Visual Studio Code
**Per desplegaments urgents o quan GitHub falla**

```bash
# Des del terminal de VSCode, dins de /frontend
cd frontend

# Deploy manual forçat
vercel --prod

# O deploy amb força (ignora cache)
vercel --prod --force
```

---

## ⚙️ Configuració tècnica del projecte

### 1. Configuració inicial (ja implementada)
```bash
# Instal·lar Vercel CLI (si no està instal·lat)
npm i -g vercel

# Autenticar-se a Vercel
vercel login

# Connectar projecte (ja fet)
vercel link
```

### 2. Variables d'entorn (configurades)

#### Vercel Environment Variables:
- `VITE_API_BASE_URL=https://validacio-backend.fly.dev/api`

#### Frontend `.env.production` (no necessari amb GitHub):
```bash
# Aquestes variables es gestionen des de Vercel Dashboard
VITE_API_BASE_URL=https://validacio-backend.fly.dev/api
```

### 3. Configuració de fitxers (implementats)

#### Root Directory Configuration (Vercel):
- **Root Directory**: `frontend`
- **Build Command**: `npm run build`
- **Output Directory**: `dist`
- **Install Command**: `npm install`

#### Configuració Vercel (`frontend/vercel.json`) - implementat:
```json
{
  "version": 2,
  "builds": [
    {
      "src": "package.json",
      "use": "@vercel/static-build",
      "config": { "distDir": "dist" }
    }
  ],
  "routes": [
    {
      "src": "/assets/(.*)",
      "dest": "/assets/$1"
    },
    {
      "src": "/(.*\\.(js|css|png|jpg|jpeg|gif|svg|ico|woff|woff2|ttf|eot))",
      "dest": "/$1"
    },
    {
      "src": "/(.*)",
      "dest": "/index.html"
    }
  ]
}
```

#### Configuració Vite (`frontend/vite.config.ts`) - implementat:
```typescript
import { defineConfig, loadEnv } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';

export default defineConfig(({ mode }) => {
  // Carrega variables d'entorn del fitxer corresponent (.env, .env.production, etc.)
  const env = loadEnv(mode, process.cwd());

  return {
    plugins: [react()],
    define: {
      'process.env': env, // opcional si necessites accedir a process.env.*
    },
    resolve: {
      alias: {
        '@': path.resolve(__dirname, 'src'),
      },
    },
  };
});
```

---

## 🛠️ Workflow de desenvolupament actual

### Desenvolupament local:
```bash
cd frontend
npm run dev
# L'aplicació s'executa a http://localhost:5173
```

### Actualització i desplegament (mètode principal):
```bash
# 1. Fer canvis al codi a Visual Studio Code
# 2. Commit dels canvis
git add .
git commit -m "Descripció clara dels canvis"

# 3. Push al repositori (triggerea deploy automàtic)
git push

# 4. Verificar desplegament a Vercel Dashboard
# 5. Comprovar l'aplicació a https://validacio-factures.vercel.app
```

### Desplegament manual d'emergència:
```bash
cd frontend
vercel --prod --force
```

---

## 📊 URLs i enllaços importants

### Aplicació en producció:
```
https://validacio-factures.vercel.app
```

### API Backend:
```
https://validacio-backend.fly.dev/api
```

### Vercel Dashboard:
```
https://vercel.com/francesc-hidalgo-marquezs-projects/frontend-fund
```

### Repositori GitHub:
```
https://github.com/Susisco/VALIDACIO-FACTURES
```

---

## 🔧 Comandos útils per monitorització i gestió

### Gestió del projecte:

```bash
# Ver información del proyecto
vercel ls

# Ver variables de entorno configuradas
vercel env ls

# Ver logs de deployment
vercel logs

# Abrir la aplicación en el navegador
vercel open

# Ver el estado del proyecto
vercel inspect
```

```bash
# Veure informació del projecte
vercel ls

# Veure variables d'entorn configurades  
vercel env ls

# Veure logs de desplegament
vercel logs

# Obrir l'aplicació al navegador
vercel open

# Veure l'estat del projecte
vercel inspect

# Forçar redeploy manual
vercel --prod --force
```

### Gestió de variables d'entorn:
```bash
# Afegir nova variable d'entorn
vercel env add NOMBRE_VARIABLE

# Eliminar variable d'entorn
vercel env rm NOMBRE_VARIABLE

# Llistar totes les variables
vercel env ls
```

### Gestió de GitHub i Git:
```bash
# Veure estat del repositori
git status

# Veure historial de commits
git log --oneline

# Veure branches
git branch -a

# Canviar de branch
git checkout nom-branch
```

---

## ⚙️ Configuració automàtica amb GitHub (implementat)

### Deploy automàtic configurat:
✅ **Repositori connectat**: `Susisco/VALIDACIO-FACTURES`  
✅ **Branca de producció**: `main`  
✅ **Root Directory**: `frontend`  
✅ **Build Command**: `npm run build`  
✅ **Output Directory**: `dist`  
✅ **Variables d'entorn**: Configurades a Vercel Dashboard  

### Comportament actual:
- Cada `git push` a `main` desplega automàticament
- Temps de desplegament: ~2-3 minuts
- Notificacions automàtiques de l'estat del desplegament
- Rollback automàtic si el build falla

---

## 🏗️ Arquitectura tècnica del projecte

### Stack tecnològic:
- **Frontend**: React 18 + TypeScript + Vite
- **Backend**: Spring Boot 3.4.5 (Fly.io)  
- **Base de dades**: MariaDB
- **Fitxers**: AWS S3 amb URLs signades
- **Autenticació**: JWT tokens
- **Deploy**: Vercel (frontend) + Fly.io (backend)

### Estructura del repositori:
```
VALIDACIO-FACTURES/
├── frontend/               # React app (Vercel)
│   ├── src/
│   │   ├── api/           # Client HTTP amb constants
│   │   ├── pages/         # Pàgines de l'aplicació  
│   │   ├── components/    # Components reutilitzables
│   │   └── config/        # Configuració (constants.ts)
│   ├── vercel.json        # Configuració de Vercel
│   └── vite.config.ts     # Configuració de Vite
├── backend/               # Spring Boot app (Fly.io)
│   ├── src/main/java/
│   ├── fly.toml          # Configuració de Fly.io
│   └── Dockerfile        # Imatge Docker
└── docs/                 # Documentació del projecte
    └── instruccions/     # Guies de desplegament
```

---

## 🚨 Resolució de problemes comuns

### Build falla a Vercel:
```bash
# Verificar dependències localment
cd frontend
npm install

# Provar el build localment
npm run build

# Verificar variables d'entorn a Vercel
vercel env ls

# Veure logs del darrer desplegament
vercel logs
```

### Error de CORS:
- Verificar que el backend (Fly.io) està funcionant
- Comprovar que `VITE_API_BASE_URL` està ben configurat
- Revisar la configuració CORS al backend Spring Boot

### Rutes no funcionen (404):
- Verificar que `vercel.json` té la configuració de redirects correcta
- Comprovar que React Router està configurat correctament
- Assegurar-se que les rutes estan ben definides

### Variables d'entorn no disponibles:
```bash
# Les variables han de començar per VITE_
VITE_API_BASE_URL=https://validacio-backend.fly.dev/api

# Accés al codi:
const apiUrl = import.meta.env.VITE_API_BASE_URL;
```

### Deploy no s'activa amb Git push:
1. Verificar la connexió GitHub a Vercel Dashboard
2. Comprovar que es fa push a la branca `main`
3. Revisar els logs de webhook a Vercel
4. Fer deploy manual com a alternativa: `vercel --prod`

### Fitxers no es visualitzen:
- Comprovar que el backend (Fly.io) està actiu
- Verificar les URLs signades de S3
- Revisar la configuració de permisos AWS S3

---

## 📝 Notes importants del desenvolupament

### Variables d'entorn:
- **Variables `VITE_`**: Disponibles al client (frontend)
- **NO posar secrets** a variables del frontend
- Variables sensibles van al backend (.env de Spring Boot)

### Build automàtic:
- Vercel detecta automàticament projectes Vite
- Configuració automàtica de Node.js i dependències
- Cache intel·ligent per builds més ràpids

### Domini personalitzat:
- Configurat: `validacio-factures.vercel.app`
- Configurable a **Settings** → **Domains**

### Redirects i SPA:
- `vercel.json` configurat per React Router
- Totes les rutes apunten a `index.html`
- Gestió d'assets estàtics optimitzada

### Integració amb backend:
- URL hardcoded per evitar problemes d'entorn
- Sistema híbrid per gestionar fitxers (S3 + proxy)
- Autenticació JWT centralitzada

---

## 📈 Monitorització i logs

### Vercel Dashboard:
- **Overview**: Estat general del projecte
- **Deployments**: Historial de desplegaments
- **Functions**: (No utilitzem serverless functions)
- **Analytics**: Estadístiques d'ús
- **Settings**: Configuració del projecte

### Logs importants:
```bash
# Logs del darrer desplegament
vercel logs

# Logs en temps real (durant deploy)
vercel logs --follow

# Logs d'un desplegament específic
vercel logs [DEPLOYMENT_ID]
```

### Mètriques clau:
- **Build time**: ~30-60 segons
- **Deploy time**: 2-3 minuts total
- **Bundle size**: ~750KB (minificat i comprimit)
- **Uptime**: 99.9% (garantit per Vercel)

---

## 🔄 Historial de canvis importants

### Setembre 2025:
- ✅ Implementat sistema híbrid per fitxers (CORS fix)
- ✅ Connectat deploy automàtic amb GitHub
- ✅ Centralitzades URLs del backend (constants.ts)
- ✅ Optimitzada configuració de routing (vercel.json)
- ✅ Solucionats problemes de visualització de documents

### Funcionalitats implementades:
- Sistema d'autenticació JWT
- Gestió de factures, albarans i pressupostos
- Visualització de documents (PDF/imatges) 
- Matching automàtic factura-albarans
- Gestió d'usuaris i permisos
- Històric de canvis
- Integració completa amb AWS S3

### Arquitectura actual:
- **Frontend**: React + TypeScript + Vite (Vercel)
- **Backend**: Spring Boot + MariaDB (Fly.io)
- **Storage**: AWS S3 amb URLs signades
- **CI/CD**: GitHub Actions + Vercel + Fly.io