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
 * Descarrega un fitxer d'albarà via proxy del backend (evita CORS)
 */
export const downloadAlbaraFileProxy = async (albaraId: number): Promise<void> => {
  console.log(`📥 [fitxers.ts] Descarregant fitxer per albarà ${albaraId} via proxy backend`);
  
  try {
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
    
    console.log(`✅ [fitxers.ts] Fitxer rebut del backend, creant descàrrega...`);
    
    // Obtenim el nom del fitxer del header Content-Disposition si existeix
    const contentDisposition = response.headers.get('Content-Disposition');
    let fileName = `albara_${albaraId}.pdf`;
    if (contentDisposition) {
      const fileNameMatch = contentDisposition.match(/filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/);
      if (fileNameMatch) {
        fileName = fileNameMatch[1].replace(/['"]/g, '');
      }
    }
    
    const blob = await response.blob();
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = fileName;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
    
    console.log(`🎉 [fitxers.ts] Descàrrega completada per albarà ${albaraId}`);
  } catch (error) {
    console.error(`❌ [fitxers.ts] Error descarregant via proxy:`, error);
    throw error;
  }
};

/**
 * Obre un fitxer d'albarà en una nova pestanya
 * Utilitza l'URL directa del backend que fa la redirecció automàtica
 */
export const openAlbaraFile = async (albaraId: number): Promise<void> => {
  try {
    console.log("📂 Obrint fitxer d'albarà:", albaraId);
    
    // Primer prova la descàrrega directa via proxy backend (evita problemes CORS)
    console.log(`🔄 [fitxers.ts] Provant descàrrega directa via proxy backend...`);
    await downloadAlbaraFileProxy(albaraId);
    
  } catch (proxyError) {
    console.warn(`⚠️ [fitxers.ts] Error amb proxy backend, provant mètode original:`, proxyError);
    
    try {
      // Obtenim el token per afegir-lo als headers
      const token = localStorage.getItem("token");
      if (!token) {
        throw new Error('No estàs autenticat');
      }

      // Creem una URL que inclou l'autenticació com a query parameter
      // Això permet que el backend gestioni la redirecció directament
      const backendUrl = `https://validacio-backend.fly.dev/api/fitxers/albara/${albaraId}`;
      
      // Creem un formulari ocult per fer la petició POST amb el token
      const form = document.createElement('form');
      form.method = 'GET';
      form.action = backendUrl;
      form.target = '_blank';
      form.style.display = 'none';
      
      // Afegim el token com a header via fetch i després obrir
      const response = await fetch(backendUrl, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Accept': 'application/json'
        },
        credentials: 'include'
      });
      
      if (response.status === 302 || response.redirected) {
        // Si hi ha redirecció, obtenim la URL final
        const finalUrl = response.url;
        console.log("🔗 URL final:", finalUrl);
        window.open(finalUrl, '_blank', 'noopener,noreferrer');
      } else if (response.ok) {
        // Si és un 200, potser retorna la URL directament
        const data = await response.text();
        console.log("📄 Resposta del backend:", data);
        window.open(backendUrl, '_blank', 'noopener,noreferrer');
      } else {
        throw new Error(`Error del servidor: ${response.status}`);
      }
    } catch (originalError) {
      console.error("❌ Error obrint fitxer:", originalError);
      alert('Error en obrir el fitxer: ' + (originalError instanceof Error ? originalError.message : 'Error desconegut'));
    }
  }
};