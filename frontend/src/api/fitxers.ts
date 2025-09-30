// src/api/fitxers.ts
// Versió amb sistema proxy per evitar CORS - v1.1
import { api } from './client';

/**
 * Obté una URL temporal per veure un fitxer d'albarà des d'S3
 * Fa la petició amb autenticació i retorna la URL temporal
 */
export const getAlbaraFileUrl = async (albaraId: number): Promise<string> => {
  try {
    console.log("🔗 Obtenint URL per albarà:", albaraId);
    
    // Fem la petició amb autenticació via l'interceptor d'axios
    const response = await api.get(`/fitxers/albara/${albaraId}`, {
      // No seguir redireccions automàticament
      maxRedirects: 0,
      validateStatus: (status) => status === 302 || status === 200
    });

    console.log("📡 Resposta del backend:", response.status, response.statusText);
    console.log("📍 Headers:", response.headers);

    // Si el backend retorna una redirecció 302, obtenim la URL del header Location
    if (response.status === 302) {
      const locationHeader = response.headers.location;
      console.log("🔗 URL signada obtinguda:", locationHeader);
      if (locationHeader) {
        return locationHeader;
      }
    }

    throw new Error('No s\'ha pogut obtenir la URL del fitxer');
  } catch (error: any) {
    console.error("❌ Error obtenint URL:", error);
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
 * Primer prova el mètode original, després utilitza proxy si falla
 */
export const openAlbaraFile = async (albaraId: number): Promise<void> => {
  try {
    console.log("📂 Obrint fitxer d'albarà:", albaraId);
    
    // PRIMER: Prova el mètode original (simple i ràpid)
    try {
      console.log(`� [fitxers.ts] Provant mètode original (URL directa)...`);
      const fileUrl = await getAlbaraFileUrl(albaraId);
      console.log(`📁 [fitxers.ts] URL obtinguda:`, fileUrl);
      
      // Prova d'obrir directament
      window.open(fileUrl, '_blank', 'noopener,noreferrer');
      console.log(`✅ [fitxers.ts] Fitxer obert amb mètode original`);
      return; // Èxit! No cal continuar
      
    } catch (originalError) {
      console.warn(`⚠️ [fitxers.ts] Mètode original fallit (probablement CORS):`, originalError);
      
      // SEGON: Si falla, utilitza el proxy per mostrar el contingut
      console.log(`🔄 [fitxers.ts] Provant amb proxy backend...`);
      
      const token = localStorage.getItem("token");
      if (!token) {
        throw new Error('No estàs autenticat');
      }

      const response = await fetch(`https://validacio-backend.fly.dev/api/fitxers/albara/${albaraId}/download`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Accept': 'application/pdf,image/*,*/*'
        },
        credentials: 'include'
      });
      
      if (!response.ok) {
        throw new Error(`Error del servidor: ${response.status} ${response.statusText}`);
      }
      
      console.log(`✅ [fitxers.ts] Contingut rebut via proxy, obrint en nova pestanya...`);
      
      // Creem un blob URL per mostrar el contingut en nova pestanya
      const blob = await response.blob();
      const blobUrl = URL.createObjectURL(blob);
      
      // Obrim en nova finestra/pestanya
      const newWindow = window.open(blobUrl, '_blank', 'noopener,noreferrer');
      
      if (!newWindow) {
        console.warn(`⚠️ [fitxers.ts] Popup bloquejat, creant enllaç temporal...`);
        // Si el popup està bloquejat, creem un enllaç i el cliquegem
        const link = document.createElement('a');
        link.href = blobUrl;
        link.target = '_blank';
        link.click();
      }
      
      // Alliberem la memòria després d'un temps
      setTimeout(() => {
        URL.revokeObjectURL(blobUrl);
      }, 60000); // 1 minut
      
      console.log(`🎉 [fitxers.ts] Fitxer mostrat via proxy per albarà ${albaraId}`);
    }
    
  } catch (error) {
    console.error(`❌ [fitxers.ts] Error obrint fitxer per albarà ${albaraId}:`, error);
    alert('Error en obrir el fitxer: ' + (error instanceof Error ? error.message : 'Error desconegut'));
  }
};