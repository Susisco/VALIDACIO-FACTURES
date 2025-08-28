import React from 'react';
import { Container, Loader, Table, Text, Title } from '@mantine/core';
import { useDeviceVersions } from '../api/devices';

export default function DeviceAdminPage() {
  const { data, isLoading, error } = useDeviceVersions();

  if (isLoading)
    return (
      <Container py="xl" style={{ textAlign: 'center' }}>
        <Loader />
      </Container>
    );
  if (error)
    return (
      <Container py="xl">
        <Text color="red">Error carregant versions</Text>
      </Container>
    );
  if (!data?.length)
    return (
      <Container py="xl">
        <Text>No hi ha dades.</Text>
      </Container>
    );

  return (
    <Container py="xl">
      <Title order={2}>VERSIONS D'APLICACIÓ</Title>
      <Table striped highlightOnHover mt="md">
        <thead>
          <tr>
            <th>Versió</th>
            <th>Nombre de dispositius</th>
          </tr>
        </thead>
        <tbody>
          {data.map((v) => (
            <tr key={v.appVersion ?? 'unknown'}>
              <td>{v.appVersion ?? 'Desconeguda'}</td>
              <td>{v.count}</td>
            </tr>
          ))}
        </tbody>
      </Table>
    </Container>
  );
}
