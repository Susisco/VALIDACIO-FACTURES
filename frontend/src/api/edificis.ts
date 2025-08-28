// src/api/edificis.ts
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {api} from './client';
export interface Edifici { id: number; nom: string; alias: string; ubicacio: string; }
export type EdificiInput = Omit<Edifici, 'id'>;
export const getEdificis = (): Promise<Edifici[]> => api.get('/edificis').then(r => r.data);
export const getEdifici = (id: number): Promise<Edifici> => api.get(`/edificis/${id}`).then(r => r.data);
export const createEdifici = (d: EdificiInput): Promise<Edifici> => api.post('/edificis', d).then(r => r.data);
export const updateEdifici = (id: number, d: EdificiInput): Promise<Edifici> => api.put(`/edificis/${id}`, d).then(r => r.data);
export const deleteEdifici = (id: number): Promise<{ success: boolean }> => api.delete(`/edificis/${id}`).then(r => r.data);
export const useEdificis = () => useQuery({ queryKey: ['edificis'], queryFn: getEdificis });
export const useEdifici = (id: number) => useQuery({ queryKey: ['edifici', id], queryFn: () => getEdifici(id) });
export const useCreateEdifici = () => {
	const qc = useQueryClient();
	return useMutation({
		mutationFn: createEdifici,
		onSuccess: () => qc.invalidateQueries({ queryKey: ['edificis'] })
	});
};
export const useUpdateEdifici = () => {
	const qc = useQueryClient();
	return useMutation({
		mutationFn: ({ id, data }: { id: number; data: EdificiInput }) => updateEdifici(id, data),
		onSuccess: () => qc.invalidateQueries({ queryKey: ['edificis'] })
	});
};
export const useDeleteEdifici = () => {
	const qc = useQueryClient();
	return useMutation({
		mutationFn: deleteEdifici,
		onSuccess: () => qc.invalidateQueries({ queryKey: ['edificis'] })
	});
};
