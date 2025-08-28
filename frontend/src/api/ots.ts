// src/api/ots.ts
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {api} from './client';
export interface Ots { id: number; codi: string; descripcio: string; }
export type OtsInput = Omit<Ots, 'id'>;
export const getOts = (): Promise<Ots[]> => api.get('/ots').then(r => r.data);
export const getOtsById = (id: number): Promise<Ots> => api.get(`/ots/${id}`).then(r => r.data);
export const createOts = (d: OtsInput): Promise<Ots> => api.post('/ots', d).then(r => r.data);
export const updateOts = (id: number, d: OtsInput): Promise<Ots> => api.put(`/ots/${id}`, d).then(r => r.data);
export const deleteOts = (id: number): Promise<{ success: boolean }> => api.delete(`/ots/${id}`).then(r => r.data);
export const useOts = () => useQuery({ queryKey: ['ots'], queryFn: getOts });
export const useOtsById = (id: number) => useQuery({ queryKey: ['ots', id], queryFn: () => getOtsById(id) });
export const useCreateOts = () => { 
	const qc = useQueryClient(); 
	return useMutation({ 
		mutationFn: createOts, 
		onSuccess: () => qc.invalidateQueries({ queryKey: ['ots'] }) 
	}); 
};
export const useUpdateOts = () => { 
	const qc = useQueryClient(); 
	return useMutation({ 
		mutationFn: ({ id, data }: { id: number; data: OtsInput }) => updateOts(id, data), 
		onSuccess: () => qc.invalidateQueries({ queryKey: ['ots'] }) 
	}); 
};
export const useDeleteOts = () => { 
	const qc = useQueryClient(); 
	return useMutation({ 
		mutationFn: deleteOts, 
		onSuccess: () => qc.invalidateQueries({ queryKey: ['ots'] }) 
	}); 
};
