import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { api } from "./client";

// Interfície del Proveïdor que ve del backend
export interface Proveidor {
  id: number;
  nomComercial: string;
  nom: string;
  nif: string;
  adreca: string;
  // Afegeix més si el backend en retorna més
}

// Pressupost rebut del backend (amb objecte proveidor)
export interface Pressupost {
  factura: any;
  id: number;
  tipus: string;
  referenciaDocument: string;
  data: string; // "YYYY-MM-DD"
  importTotal: number;
  estat: string;
  creadorId: number;
  validatPerId: number | null;
  proveidor: Proveidor; // ✅ canvi aquí
  fitxerAdjunt: string | null;
  creat: string;
  actualitzat: string;
  edificiId: number;
  otsId: number;
  facturaId: number | null;
}

// Input per crear o actualitzar: només volem l'id del proveïdor
export type PressupostInput = Omit<
  Pressupost,
  "id" | "creat" | "actualitzat" | "proveidor"
> & {
  proveidorId: number;
};

// Fetch all
export const getPressupostos = (): Promise<Pressupost[]> => {
  return api.get("/pressupostos").then((res) => res.data);
};

// Fetch by id
export const getPressupost = (id: number): Promise<Pressupost> => {
  return api.get(`/pressupostos/${id}`).then((res) => res.data);
};

// Create
export const createPressupost = (
  data: PressupostInput
): Promise<Pressupost> => {
  return api.post("/pressupostos", data).then((res) => res.data);
};

// Update
export const updatePressupost = (
  id: number,
  data: PressupostInput
): Promise<Pressupost> => {
  return api.put(`/pressupostos/${id}`, data).then((res) => res.data);
};

// Delete
export const deletePressupost = (
  id: number
): Promise<{ success: boolean }> => {
  return api.delete(`/pressupostos/${id}`).then((res) => res.data);
};

// React Query hooks
export const usePressupostos = () =>
  useQuery({ queryKey: ["pressupostos"], queryFn: getPressupostos });

export const usePressupost = (id: number) =>
  useQuery({ queryKey: ["pressupost", id], queryFn: () => getPressupost(id) });

export const useCreatePressupost = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: createPressupost,
    onSuccess: () => qc.invalidateQueries({ queryKey: ["pressupostos"] }),
  });
};

export const useUpdatePressupost = () => {
  const qc = useQueryClient();
  return useMutation<Pressupost, Error, { id: number; data: PressupostInput }>(
    {
      mutationFn: ({ id, data }) => updatePressupost(id, data),
      onSuccess: () => {
        qc.invalidateQueries({ queryKey: ["pressupostos"] });
      },
      onError: (error) => {
        console.error("Error en la actualización:", error);
      },
    }
  );
};

export const useDeletePressupost = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: deletePressupost,
    onSuccess: () => qc.invalidateQueries({ queryKey: ["pressupostos"] }),
  });
};
