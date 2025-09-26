# Deploy Frontend React a Vercel

## Resumen de pasos para hacer deploy del frontend

### 1. Instalación y configuración inicial
```bash
# Instalar Vercel CLI (si no está instalado)
npm i -g vercel

# Autenticarse en Vercel
vercel login
```

### 2. Configuración del proyecto

#### Variables de entorno (`frontend/.env.production`)
```bash
# URL de l'API del backend. Aquesta variable s'utilitza per fer peticions al servidor backend.
# Assegura't que aquesta URL sigui accessible des del frontend.
VITE_API_BASE_URL=https://validacio-backend.fly.dev/api
```

#### Configuración Vercel (`frontend/vercel.json`)
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
    { "src": "/(.*)", "dest": "/index.html" }
  ]
}
```

#### Configuración Vite (`frontend/vite.config.ts`)
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

### 3. Comandos de deploy

#### Deploy inicial (primera vez)
```bash
cd frontend

# Linkear el proyecto con Vercel
vercel link

# Configurar variable de entorno
vercel env add VITE_API_BASE_URL production
# Cuando te pregunte el valor, introduce: https://validacio-backend.fly.dev/api

# Hacer el primer deploy
vercel --prod
```

#### Deploy posteriores (actualizaciones)
```bash
cd frontend
vercel --prod
```

### 4. Comandos útiles para monitorización

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

### 5. Configuración de variables de entorno

#### Desde la terminal:
```bash
# Añadir nueva variable de entorno
vercel env add NOMBRE_VARIABLE

# Eliminar variable de entorno
vercel env rm NOMBRE_VARIABLE

# Listar todas las variables
vercel env ls
```

#### Desde el Dashboard de Vercel:
1. Ve a [vercel.com](https://vercel.com) y accede a tu proyecto
2. Ve a **Settings** → **Environment Variables**
3. Añadir/Editar/Eliminar variables según necesites

### 6. Configuración automática con GitHub

#### Para deploy automático:
1. Conecta tu repositorio GitHub a Vercel
2. En **Settings** → **Git**, configura:
   - **Production Branch**: `main`
   - **Build Command**: `npm run build`
   - **Output Directory**: `dist`
3. Cada `git push` a la rama `main` desplegará automáticamente

### 7. URLs de la aplicación

Una vez desplegada, la aplicación estará disponible en:
```
https://tu-proyecto.vercel.app
```

En nuestro caso:
```
https://frontend-bi43tjcbk-francesc-hidalgo-marquezs-projects.vercel.app
```

## Notas importantes

- **Variables de entorno**: Las variables que empiecen por `VITE_` estarán disponibles en el cliente. No pongas secretos aquí.

- **Build automático**: Vercel detecta automáticamente que es un proyecto Vite y usa la configuración correcta.

- **Dominio personalizado**: Puedes configurar un dominio personalizado en **Settings** → **Domains**.

- **Redirects**: El archivo `vercel.json` está configurado para que todas las rutas apunten a `index.html` (necesario para React Router).

## Estructura del proyecto frontend

```
frontend/
├── .env.production          # Variables de entorno para producción
├── vercel.json             # Configuración de Vercel
├── vite.config.ts          # Configuración de Vite
├── package.json            # Dependencias y scripts
├── src/
│   ├── api/
│   │   └── client.ts       # Cliente HTTP que usa VITE_API_BASE_URL
│   ├── pages/              # Páginas de la aplicación
│   ├── components/         # Componentes reutilizables
│   └── ...
└── dist/                   # Archivos generados por el build (no subir a Git)
```

## Solución de problemas comunes

### Build falla
```bash
# Verificar dependencias
npm install

# Probar el build localmente
npm run build

# Verificar variables de entorno
vercel env ls
```

### Error de CORS
- Verificar que `VITE_API_BASE_URL` apunte al backend correcto
- Comprobar que el backend acepta requests desde el dominio de Vercel

### Rutas no funcionan (404)
- Verificar que `vercel.json` tiene la configuración de redirects
- Comprobar que React Router está configurado correctamente

### Variables de entorno no disponibles
```bash
# Las variables deben empezar por VITE_
VITE_API_BASE_URL=https://validacio-backend.fly.dev/api

# Acceso en el código:
const apiUrl = import.meta.env.VITE_API_BASE_URL;
```

## Workflow completo de desarrollo

### 1. Desarrollo local:
```bash
cd frontend
npm run dev
# La app se ejecuta en http://localhost:5173
```

### 2. Actualizar y desplegar:
```bash
# Hacer cambios en el código
git add .
git commit -m "Actualizar frontend"
git push

# Deploy manual (si no tienes auto-deploy configurado)
vercel --prod
```

### 3. Verificar deployment:
- Acceder a la URL de producción
- Verificar que la conexión con el backend funciona
- Comprobar logs si hay errores