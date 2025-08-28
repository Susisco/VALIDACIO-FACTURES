// src/api/proveidors.ts
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import {api} from './client';
export interface Proveidor { id: number; nomComercial: string; nom: string; nif: string; adreca: string; }
export type ProveidorInput = Omit<Proveidor, 'id'>;
export const getProveidors = (): Promise<Proveidor[]> => api.get('/proveidors').then(r => r.data);
export const getProveidor = (id: number): Promise<Proveidor> => api.get(`/proveidors/${id}`).then(r => r.data);
export const createProveidor = (d: ProveidorInput): Promise<Proveidor> => api.post('/proveidors', d).then(r => r.data);
export const updateProveidor = (id: number, d: ProveidorInput): Promise<Proveidor> => api.put(`/proveidors/${id}`, d).then(r => r.data);
export const deleteProveidor = (id: number): Promise<{ success: boolean }> => api.delete(`/proveidors/${id}`).then(r => r.data);

// Hooks
// useProveidors – obtenir totes les proveidors
export const useProveidors = () => useQuery({ queryKey: ['proveidors'], queryFn: getProveidors });
// useProveidor – obtenir una sola proveidor per id
export const useProveidor = (id: number) => useQuery({ queryKey: ['proveidor', id], queryFn: () => getProveidor(id) });
// useCreateProveidor – crear una nova proveidor
export const useCreateProveidor = () => {
    const qc = useQueryClient();
    return useMutation<Proveidor, Error, ProveidorInput>({
        mutationFn: createProveidor,
        onSuccess: () => qc.invalidateQueries({ queryKey: ['proveidors'] }),
    });
};
// useUpdateProveidor – actualitzar una proveidor
export const useUpdateProveidor = () => {
  const qc = useQueryClient();
  return useMutation<Proveidor, Error, { id: number; data: ProveidorInput }>({
    mutationFn: ({ id, data }) => updateProveidor(id, data),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['proveidors'] })
  });
};
// useDeleteProveidor – eliminar una proveidor
export const useDeleteProveidor = () => {
  const qc = useQueryClient();
  return useMutation<{ success: boolean }, Error, number>({
    mutationFn: deleteProveidor,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['proveidors'] }),
  });
};

