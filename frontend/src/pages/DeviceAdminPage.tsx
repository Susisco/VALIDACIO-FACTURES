import React from 'react';
import { Button, Container, Loader, Table, Text, Title } from '@mantine/core';
import { useDevices, useApproveDevice, useRevokeDevice } from '../api/devices';

export default function DeviceAdminPage() {
  const { data: devices, isLoading, error } = useDevices();
  const approve = useApproveDevice();
  const revoke = useRevokeDevice();

  if (isLoading)
    return (
      <Container py="xl" style={{ textAlign: 'center' }}>
        <Loader />
      </Container>
    );
  if (error)
    return (
      <Container py="xl">
        <Text color="red">Error: {(error as Error).message}</Text>
      </Container>
    );
  if (!devices?.length)
    return (
      <Container py="xl">
        <Text>No hi ha dispositius.</Text>
      </Container>
    );

  return (
    <Container py="xl" style={{ fontFamily: 'Poppins, sans-serif' }}>
      <Title order={2}>DISPOSITIUS</Title>
      <Table striped highlightOnHover>
        <thead>
          <tr>
            <th>FID</th>
            <th>Estat</th>
            <th>Accions</th>
          </tr>
        </thead>
        <tbody>
          {devices.map((d) => (
            <tr key={d.fid}>
              <td>{d.fid}</td>
              <td>{d.status}</td>
              <td>
                <Button size="xs" mr="xs" onClick={() => approve.mutate(d.fid)}>
                  Aprova
                </Button>
                <Button
                  size="xs"
                  color="red"
                  variant="outline"
                  onClick={() => revoke.mutate(d.fid)}
                >
                  Revoca
                </Button>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
    </Container>
  );
}
