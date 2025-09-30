// src/api/fitxers.ts
import { api } from './client';

/**
 * Obté una URL temporal per veure un fitxer d'albarà des d'S3
 * Fa la petició amb autenticació i retorna la URL temporal
 */
export const getAlbaraFileUrl = async (albaraId: number): Promise<string> => {
  try {
    // Fem la petició amb autenticació via l'interceptor d'axios
    const response = await api.get(`/fitxers/albara/${albaraId}`, {
      // No seguir redireccions automàticament
      maxRedirects: 0,
      validateStatus: (status) => status === 302 || status === 200
    });

    // Si el backend retorna una redirecció 302, obtenim la URL del header Location
    if (response.status === 302) {
      const locationHeader = response.headers.location;
      if (locationHeader) {
        return locationHeader;
      }
    }

    throw new Error('No s\'ha pogut obtenir la URL del fitxer');
  } catch (error: any) {
    if (error.response?.status === 404) {
      throw new Error('Fitxer no trobat');
    } else if (error.response?.status === 403) {
      throw new Error('No tens permisos per veure aquest fitxer');
    } else {
      throw new Error('Error en obtenir la URL del fitxer: ' + (error.message || 'Error desconegut'));
    }
  }
};

/**
 * Obre un fitxer d'albarà en una nova pestanya
 * Gestiona l'autenticació automàticament
 */
export const openAlbaraFile = async (albaraId: number): Promise<void> => {
  try {
    const fileUrl = await getAlbaraFileUrl(albaraId);
    window.open(fileUrl, '_blank', 'noopener,noreferrer');
  } catch (error: any) {
    alert(error.message || 'Error en obrir el fitxer');
  }
};