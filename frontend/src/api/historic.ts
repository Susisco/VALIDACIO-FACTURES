import { api } from './client'; // Importa la instància de l'API, client axios configurat
import { useQuery } from "@tanstack/react-query";

export interface HistoricCanvi {
  id: number;
  tipusDocument: string;
  documentId: number;
  usuari: {
    id: number;
    nom: string;
  };
  dataHora: string;
  descripcio: string;
}

export function useHistoricCanvis(tipus: string, documentId: number) {
  return useQuery<HistoricCanvi[]>({
    queryKey: ["historicCanvis", tipus, documentId],
    queryFn: async () => {
      const response = await api.get(`/historic/${tipus.toLowerCase()}/${documentId}`);
      return response.data;
    },
    enabled: !!documentId,
  });
}
