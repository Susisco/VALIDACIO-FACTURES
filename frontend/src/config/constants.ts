// src/config/constants.ts
// Configuració centralitzada d'URLs

// URL del backend - hardcoded per estabilitat
export const API_BASE_URL = "https://validacio-backend.fly.dev/api";

// URL base per debug
console.log("🔧 API_BASE_URL configurada:", API_BASE_URL);
console.log("🌍 VITE_API_BASE_URL des d'entorn:", import.meta.env.VITE_API_BASE_URL);