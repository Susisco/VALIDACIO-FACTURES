// src/main.tsx
import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import { MantineProvider } from '@mantine/core';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { builder } from '@builder.io/react';
import RoutesProvider from './routes';
//import App from './App';
import './builder-registry';

// Inicializa Builder.io con tu API Key
builder.init('TU_API_KEY_DE_BUILDER');

const queryClient = new QueryClient();

ReactDOM.createRoot(document.getElementById('root') as HTMLElement).render(
  <React.StrictMode>
    <MantineProvider>
      <QueryClientProvider client={queryClient}>
        <BrowserRouter>
          <RoutesProvider>
            {/* <App /> */}
            {/* Aquí puedes usar el componente de Builder.io si lo necesitas */}
            {/* <BuilderComponent model="page" content={content} /> */}
          </RoutesProvider>
        </BrowserRouter>
      </QueryClientProvider>
    </MantineProvider>
  </React.StrictMode>
);