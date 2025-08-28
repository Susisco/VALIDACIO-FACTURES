// src/api/usuaris.ts
//import { useQuery, useMutation, useQueryClient } from 'react-query';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import {api, getAuthHeader} from './client';

// Interfície Usuari que ve del backend
export interface Usuari { id: number; nom: string; email: string; contrasenya: string; rol: string;contrasenyaTemporal: boolean; }
export type UsuariInput = Omit<Usuari, 'id'>;
export const getUsuaris = (): Promise<Usuari[]> => api.get('/usuaris').then(r => r.data);
export const getUsuari = (id: number): Promise<Usuari> => api.get(`/usuaris/${id}`).then(r => r.data);
export const createUsuari = (d: UsuariInput): Promise<Usuari> => api.post('/usuaris', d).then(r => r.data);
export const updateUsuari = (id: number, d: UsuariInput): Promise<Usuari> => api.put(`/usuaris/${id}`, d).then(r => r.data);
export const deleteUsuari = (id: number): Promise<{ success: boolean }> => api.delete(`/usuaris/${id}`).then(r => r.data);
export const useUsuaris = () => useQuery({ queryKey: ['usuaris'], queryFn: getUsuaris });
export const useUsuari = (id: number) => useQuery({ queryKey: ['usuari', id], queryFn: () => getUsuari(id) });
export const useCreateUsuari = () => { 
	const qc = useQueryClient(); 
	return useMutation<Usuari, Error, UsuariInput>({
		mutationFn: createUsuari,
		onSuccess: () => {
			qc.invalidateQueries({ queryKey: ['usuaris'] });
		}
	});
};

export const useUpdateUsuari = () => { 
	const qc = useQueryClient(); 
	return useMutation<Usuari, Error, { id: number; data: UsuariInput }>({
		mutationFn: ({ id, data }) => updateUsuari(id, data), 
		onSuccess: () => qc.invalidateQueries({ queryKey: ['usuaris'] })
	}); 
};

export const useDeleteUsuari = () => { 
	const qc = useQueryClient(); 
	return useMutation<{ success: boolean }, Error, number>({
		mutationFn: (id) => deleteUsuari(id), 
		onSuccess: () => qc.invalidateQueries({ queryKey: ['usuaris'] })
	}); 
};


// api/usuaris.ts

export interface Usuari {
  id: number;
  nom: string;
  email: string;
  rol: string;
}





export const resetPasswordUsuari = async (id: number): Promise<void> => {
  await api.post(`/usuaris/reset-password/${id}`);
};




//*DUES FORMES DE CANVI DE CONTRASENYA*//
//1-  🔧 API: Canvi de contrasenya obligatori, directe sense hook
export const changePassword = async (oldPassword: string, newPassword: string): Promise<void> => {
  await api.post("/auth/change-password", {
    oldPassword,
    newPassword,
  });
};

//2- 🔧 Hook: Canvi de contrasenya, useMutation si ho vols gestionar de forma reactiva (amb loading, errors, etc):
export const useChangePassword = () => {
  return useMutation<void, Error, { oldPassword: string; newPassword: string }>({
    mutationFn: ({ oldPassword, newPassword }) =>
      api.post("/auth/change-password", { oldPassword, newPassword }).then((r) => r.data),
  });
};

