// src/api/client.ts
import axios from "axios";

export const CLIENT_PLATFORM_HEADER = "X-Client-Platform";
export const WEB_PLATFORM_VALUE = "WEB";

// Configuració dinàmica segons entorn
const getBaseURL = (): string => {
  // 1. Prioritat màxima: Variable d'entorn explícita
  if (import.meta.env.VITE_API_BASE_URL) {
    return import.meta.env.VITE_API_BASE_URL;
  }
  
  // 2. Fallback segons entorn de desenvolupament
  if (import.meta.env.DEV) {
    return "http://localhost:8080/api"; // Desenvolupament local
  }
  
  // 3. Fallback per producció
  return "https://validacio-backend.fly.dev/api"; // Producció
};

// Instància d'Axios
export const api = axios.create({
  baseURL: getBaseURL(),
  // ❌ NO definim Content-Type global
});

// Debug: Mostrar configuració actual (només en desenvolupament)
if (import.meta.env.DEV) {
  console.log(`🔗 API Base URL: ${getBaseURL()}`);
  console.log(`🌍 Environment: ${import.meta.env.MODE}`);
}

// ✅ Interceptor de petició — afegeix plataforma i token
api.interceptors.request.use((config) => {
  config.headers = config.headers ?? {};
  
  // Afegir header de plataforma
  config.headers[CLIENT_PLATFORM_HEADER] = WEB_PLATFORM_VALUE;
  
  // Afegir token JWT si existeix
  const token = localStorage.getItem("token")?.trim();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  
  return config;
});

// 🔐 Funció per obtenir el token (per compatibilitat)
export const getAuthHeader = () => {
  const token = localStorage.getItem("token")?.trim();
  if (!token) {
    throw new Error("Token no disponible. Si us plau, inicia sessió.");
  }
  return `Bearer ${token}`;
};

// ✅ Interceptor de resposta — gestiona errors 401 automàticament
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error?.response?.status;
    if (status === 401) {
      alert("401 Sessió expirada. Torna a iniciar sessió.");
      localStorage.removeItem("token");
      localStorage.removeItem("nom");
      window.location.href = "/login";
    } else if (status === 403) {
      alert("403 No tens permisos per fer aquesta acció.");
    } else if (status === 404) {
      alert("404 El recurs sol·licitat no existeix.");
    } else if (status >= 500) {
      alert("500 Error al servidor. Torna-ho a provar més tard.");
    }

    return Promise.reject(error);
  }
);

// Per si encara uses fetch en algun lloc:
export async function handleApiError(response: Response): Promise<void> {
  if (!response.ok) {
    const text = await response.text();
    throw new Error(`Error ${response.status}: ${text}`);
  }
}
