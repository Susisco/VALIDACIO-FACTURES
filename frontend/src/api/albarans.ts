import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { api, getAuthHeader, handleApiError } from "./client";
import { Usuari } from "./usuaris";
//import { saveAlbaraWithFile } from "../api/albarans";

// Interfície del Proveïdor que ve del backend
export interface Proveidor {
  id: number;
  nomComercial: string;
  nom: string;
  nif: string;
  adreca: string;
  // Afegeix més si el backend en retorna més
}
export interface Albara {
  id: number;
  tipus: string;
  referenciaDocument: string;
  data: string; // "YYYY-MM-DD"
  importTotal: number;
  estat: string;
  creadorId: number;
  creador?: Usuari; // Objecte usuari creador
  validatPerId: number | null;
  proveidor: Proveidor; // Objecte proveidor
  fitxerAdjunt: string | null;
  creat: string;
  actualitzat: string;
  edificiId: number;
  otsId: number;
  factura: { id: number; referenciaDocument?: string } | null;
  usuariModificacio?: {
    id: number;
    nom: string;
  };
}

//AlbaraInput define la estructura de los datos necesarios
//  para crear o actualizar un albarán
export interface AlbaraInput {
  tipus: string;
  referenciaDocument: string;
  data: string; // "YYYY-MM-DD"
  importTotal: number;
  estat: string;
  creadorId?: number;
  validatPerId: number | null;
  proveidorId: number;
  fitxerAdjunt: string | null;
  edificiId: number | null;
  otsId: number | null;
  facturaId: number | null;
}

//######################################################
// funcines puras que llaman a l'API per obtenir dades
//######################################################

// Fetch all, obtenir/recuperar totes les albarans desde l'API
export const getAlbarans = (): Promise<Albara[]> =>
  api.get("/albarans").then((r) => r.data);

// Fetch by id, obtenir/recuperar una albara per id
export const getAlbara = (id: number): Promise<Albara> =>
  api.get(`/albarans/${id}`).then((r) => r.data);

// Update, actualitzar una albara
export const updateAlbara = (id: number, d: AlbaraInput): Promise<Albara> =>
  api.put(`/albarans/${id}`, d).then((r) => r.data);

// Delete, eliminar una albara
export const deleteAlbara = (id: number): Promise<{ success: boolean }> =>
  api.delete(`/albarans/${id}`).then((r) => r.data);



//**********************************************************
// PUJAR FITXER I ALBARÀ
// Aquesta funció puja un fitxer i crea un albarà al mateix temps
// El fitxer es puja a l'API i l'albarà es crea amb la ruta del fitxer
/*
export const saveAlbaraWithFile = async (
  data: AlbaraInput,
  file: File
): Promise<{ success: boolean; message: string }> => {
  const formData = new FormData();
  formData.append(
    "data",
    new Blob([JSON.stringify(data)], { type: "application/json" })
  );
  formData.append("file", file);

  const token = localStorage.getItem("token")?.trim(); // Elimina espacios adicionales

  if (!token) {
    throw new Error("Token no disponible. Por favor, inicia sesión.");
  }

  const response = await fetch(
    `${import.meta.env.VITE_API_BASE_URL}/albarans/save-with-file`,
    {
      method: "POST",
      body: formData,
      headers: {
        Authorization: getAuthHeader(),
      },
    }
  );

  await handleApiError(response);

  return response.json(); // Retorna la respuesta del backend
};
*/

// Pujar fitxer + crear albarà (multipart/form-data)
export const saveAlbaraWithFile = async (
  data: AlbaraInput,
  file: File
): Promise<Albara> => {
  const form = new FormData();
  form.append("data", new Blob([JSON.stringify(data)], { type: "application/json" }));
  form.append("file", file);

  // IMPORTANT:
  // - No posem Content-Type: axios el deixarà perquè el navegador afegeixi el boundary
  // - El token s'afegeix via l'interceptor d'axios (api.interceptors.request)
  const res = await api.post("/albarans/save-with-file", form);
  return res.data as Albara;
};

//######################################################
// hooks que encapsulen les funcions per a fer servir amb react-query
// es el que cridem des de les vistes
//######################################################

// Hook per GET /albarans TOTES les albarans
export const useAlbarans = () =>
  useQuery({ queryKey: ["albarans"], queryFn: getAlbarans });

// useAlbara (nou) – obtenir una sola albara per id
export const useAlbara = (id: number) =>
  useQuery({ queryKey: ["albara", id], queryFn: () => getAlbara(id) });

// useUpdateAlbara (nou) – actualitzar una albara
export const useUpdateAlbara = () => {
  const qc = useQueryClient();
  return useMutation<Albara, Error, { id: number; data: AlbaraInput }>({
    mutationFn: ({ id, data }: { id: number; data: AlbaraInput }) =>
      updateAlbara(id, data),
    mutationKey: ["updateAlbara"],
    onSuccess: () => qc.invalidateQueries({ queryKey: ["albarans"] }),
  });
};

export const useDeleteAlbara = () => {
  const qc = useQueryClient();
  return useMutation<{ success: boolean }, Error, number>({
    mutationFn: (id: number) => deleteAlbara(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["albarans"] }),
  });
};


//***********************************************************************
// Hook per pujar fitxer i crear albarà alhora
export const useSaveAlbaraWithFile = () => {
  return useMutation({
    mutationFn: ({ data, file }: { data: AlbaraInput; file: File }) =>
      saveAlbaraWithFile(data, file),
    onError: (error: Error) => {
      // Manejo de errores en el hook
      if (error.message === "Sesión expirada") {
        console.error("Redirigiendo al usuario al login...");
      } else {
        console.error("Error al guardar el albarà:", error);
      }
    },
  });
};
