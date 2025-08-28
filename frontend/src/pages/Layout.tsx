// src/pages/Layout.tsx
import React, { useEffect, useState } from 'react';
import Header from '../components/Header';
import { Text, Container, Title, Button } from '@mantine/core';
import { useLocation, useNavigate } from 'react-router-dom';
import ContrasenyaModal from '../components/ContrasenyaModal';

export default function Layout({ children }: { children?: React.ReactNode }) {
  const location = useLocation();
  const navigate = useNavigate();
  const token = localStorage.getItem('token');
  const contrasenyaTemporal = localStorage.getItem('contrasenyaTemporal') === 'true';

  const [modalVisible, setModalVisible] = useState(false);

  const isLoggedIn = !!token;
  const isRootPage = location.pathname === '/';
  const showWelcome = !isLoggedIn && isRootPage;

  useEffect(() => {
    if (isLoggedIn && contrasenyaTemporal) {
      setModalVisible(true);
    }
  }, [isLoggedIn, contrasenyaTemporal]);

  return (
    <div style={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
      <header style={{ flexShrink: 0, marginTop: '0px' }}>
        <Header />
      </header>

      <main
        style={{
          flex: 1,
          backgroundColor: '#F0F8FF',
          padding: '0px',
          display: 'flex',
          flexDirection: 'column',
        }}
      >
        <div style={{ marginTop: '60px', padding: '50px', fontFamily: 'Poppins, sans-serif' }}>
          {showWelcome ? (
            <Container size="sm">
              <Title order={2} style={{ textAlign: 'center' }} mb="lg">
                Benvingut a l’aplicació de Validació de Factures
              </Title>
              <Text style={{ textAlign: 'center' }} size="md" color="dimmed" mb="md">
                Inicia sessió per gestionar factures, pressupostos, albarans i més.
              </Text>
              <div style={{ display: 'flex', justifyContent: 'center', marginTop: '20px' }}>
                <Button onClick={() => navigate('/login')}>Inicia sessió</Button>
              </div>
            </Container>
          ) : (
            children
          )}
        </div>
      </main>

      {/* 🔐 Modal de canvi de contrasenya temporal */}
      <ContrasenyaModal
        opened={modalVisible}
        onSuccess={() => setModalVisible(false)}
      />
    </div>
  );
}
