// src/api/factures.ts
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { api } from './client';

// Tipus de resposta de l'API
export interface Factura {
  id: number;
  tipus: string;
  referenciaDocument: string;
  data: string;
  importTotal: number;
  estat: 'EN_CURS' | 'PENDENT' | 'REBUTJAT' | 'VALIDAT';
  creador: { id: number; nom: string };
  proveidor: { id: number; nomComercial: string };
  detalls: Array<{
    id: number;
    importTotalDetall: number;
    referenciaDocumentDetall: string;
    albaraRelacionatId?: number;
    referenciaAlbaraRelacionat?: string; // ✅ afegit
    importAlbaraRelacionat?: number;     // ✅ afegit
  }>;
  albaransRelacionats?: { id: number; referenciaDocument: string }[];
  pressupostosRelacionats?: { id: number; referenciaDocument: string }[];
}


// Tipus d’entrada
export interface DetallInput {
  referenciaDocumentDetall: string;
  importTotalDetall: number;
  albaraRelacionatId?: number;
  referenciaAlbaraRelacionat?: string;
  importAlbaraRelacionat?: number;
}

export interface FacturaInput {
  data: string;
  tipus: string;
  referenciaDocument: string;
  estat: 'EN_CURS' | 'PENDENT' | 'REBUTJAT' | 'VALIDAT';
  proveidorId: number;
  importTotal: number;
  detalls: DetallInput[];
}

// 🔍 Obtenir totes les factures
export const useFactures = () =>
  useQuery<Factura[], Error>({
    queryKey: ['factures'],
    queryFn: async () => {
      const { data } = await api.get<Factura[]>('/factures');
      return data;
    },
  });

// 🔍 Obtenir una factura concreta
export const useFactura = (id: number) =>
  useQuery<Factura, Error>({
    queryKey: ['factura', id],
    queryFn: async () => {
      const { data } = await api.get<Factura>(`/factures/${id}`);//petició a endpoint de backend!
      return data;
    },
    enabled: !!id,
  });

// ➕ Crear una nova factura
export const useCreateFactura = () => {
  const queryClient = useQueryClient();

  return useMutation<Factura, Error, FacturaCreateInput>({
    mutationFn: async (newFactura) => {
      const { data } = await api.post<Factura>('/factures', newFactura);
      return data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['factures'] });
    },
  });
};

// ✏️ Actualitzar una factura existent AMB DETALLS
export const useUpdateFactura = () => {
  const queryClient = useQueryClient();

  return useMutation<Factura, Error, { id: number; data: FacturaInput }>({
    mutationFn: async ({ id, data }) => {
      console.log("Datos enviados:", { id, data }); // Log de los datos enviados
      const response = await api.put<Factura>(`/factures/${id}`, data);
      console.log("Respuesta recibida:", response.data); // Log de la respuesta recibida
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['factures'] });
    },
  });
};

// ✏️ Actualitzar una factura existent AMB DETALLS
export const useUpdateNomesFactura = () => {
  const queryClient = useQueryClient();

  return useMutation<Factura, Error, { id: number; data: FacturaInput }>({
    mutationFn: async ({ id, data }) => {
      console.log("Datos enviados:", { id, data }); // Log de los datos enviados
      const response = await api.put<Factura>(`/factures/${id}/factura`, data);
      console.log("Respuesta recibida:", response.data); // Log de la respuesta recibida
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['factures'] });
    },
  });
};


// 🗑️ Esborrar una factura
export const useDeleteFactura = () => {
  const queryClient = useQueryClient();

  return useMutation<void, Error, number>({
    mutationFn: async (id) => {
      await api.delete(`/factures/${id}`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['factures'] });
    },
  });
};


// Utilitzat per enviar dades al backend (espera un objecte proveidor)
export interface FacturaCreateInput extends Omit<FacturaInput, "proveidorId"> {
  proveidor: { id: number };
}

//acutalitzar l'estat d'una factura
// src/api/factures.ts
export const useUpdateFacturaEstat = () => {
  const queryClient = useQueryClient();

  return useMutation<void, Error, { id: number; estat: 'EN_CURS' | 'PENDENT' | 'REBUTJAT' | 'VALIDAT' }>({
    mutationFn: async ({ id, estat }) => {
      await api.put(`/factures/${id}/estat`, { estat });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['factures'] });
    },
  });
};
