// Aquest codi crea una instància de QueryClient de la llibreria @tanstack/react-query amb opcions per defecte.
// Les opcions inclouen un temps de caducitat (staleTime) de 5 minuts i un màxim d'un intent de reintentar les consultes fallides.
// es per gestionar les consultes de dades en aplicacions React de manera eficient i senzilla.
//evita que les consultes es tornin obsoletes immediatament després de ser realitzades, i permet un reintentar automàtic en cas d'errors temporals.
//// Aquesta instància es pot utilitzar en tota l'aplicació per gestionar les consultes de dades i el seu estat.

import { QueryClient } from '@tanstack/react-query';

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 1000 * 60 * 5, // 5 minuts
      retry: 1,
    },
  },
});
