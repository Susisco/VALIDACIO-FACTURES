// src/api/client.ts
import axios from "axios";

// Instància d'Axios
export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL, // p.ex. https://validacio-backend.fly.dev/api
  // ❌ NO definim Content-Type global
});

// token a cada request
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token")?.trim();
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// 🔐 Funció per obtenir el token // (Opcional) Mantén això només si encara tens crides amb fetch que necessiten el header manual
export const getAuthHeader = () => {
  const token = localStorage.getItem("token")?.trim();
  if (!token) {
    throw new Error("Token no disponible. Si us plau, inicia sessió.");
  }
  return `Bearer ${token}`;
};

// ✅ Interceptor de petició — afegeix el token
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = getAuthHeader();
  }
  return config;
});

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
