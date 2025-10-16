// frontend/src/pages/DeviceAdminPage.tsx
import React from 'react';
import { Badge, Button, Container, Loader, Table, Text, Title } from '@mantine/core';
import {
  useDevices,
  useApproveDevice,
  useRevokeDevice,
  useDeviceVersions,
  useArchiveDevice,
  useDeleteLogicalDevice,
  useReactivateDevice,
} from '../api/devices';

export default function DeviceAdminPage() {
  const formatDate = (iso?: string | null) => {
    if (!iso) return '-';
    const d = new Date(iso);
    if (Number.isNaN(d.getTime())) return iso; // fallback si no és ISO
    return new Intl.DateTimeFormat('ca-ES', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
    }).format(d);
  };

  const statusColor = (s: string): string => {
    switch (s) {
      case 'APPROVED':
        return 'green';
      case 'PENDING':
        return 'yellow';
      case 'REVOKED':
        return 'red';
      case 'ARCHIVED':
        return 'gray';
      case 'DELETED':
        return 'dark';
      default:
        return 'blue';
    }
  };

  const {
    data: devices,
    isLoading: devicesLoading,
    error: devicesError,
  } = useDevices();
  const {
    data: versions,
    isLoading: versionsLoading,
    error: versionsError,
  } = useDeviceVersions();
  const approve = useApproveDevice();
  const revoke = useRevokeDevice();
  const archive = useArchiveDevice();
  const delLogical = useDeleteLogicalDevice();
  const reactivate = useReactivateDevice();

  if (devicesLoading || versionsLoading)
    return (
      <Container py="xl" style={{ textAlign: 'center' }}>
        <Loader />
      </Container>
    );

  if (devicesError || versionsError)
    return (
      <Container py="xl">
        <Text color="red">Error carregant dades</Text>
      </Container>
    );

  return (
    <Container py="xl" style={{ fontFamily: 'Poppins, sans-serif' }}>
      <Title order={2}>DISPOSITIUS</Title>
      {devices && devices.length ? (
        <Table striped highlightOnHover>
          <thead>
            <tr>
              <th>FID</th>
              <th>User ID</th>
              <th>Versió</th>
              <th>Created</th>
              <th>Last Seen</th>
              <th>Archived</th>
              <th>Deleted</th>
              <th>Estat</th>
              <th>Accions</th>
            </tr>
          </thead>
          <tbody>
            {devices.map((d) => (
              <tr key={d.fid}>
                <td>{d.fid}</td>
                <td>{d.userId ?? '-'}</td>
                <td>{d.appVersion ?? '-'}</td>
                <td>{formatDate(d.createdAt)}</td>
                <td>{formatDate(d.lastSeenAt)}</td>
                <td>{formatDate(d.archivedAt)}</td>
                <td>{formatDate(d.deletedAt)}</td>
                <td>
                  <Badge color={statusColor(d.status)} variant="filled">{d.status}</Badge>
                </td>
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
                  <Button size="xs" ml="xs" variant="light" onClick={() => archive.mutate(d.fid)}>
                    Arxiva
                  </Button>
                  <Button size="xs" ml="xs" color="dark" variant="outline" onClick={() => delLogical.mutate(d.fid)}>
                    Baixa lògica
                  </Button>
                  <Button size="xs" ml="xs" color="green" variant="outline" onClick={() => reactivate.mutate(d.fid)}>
                    Reactiva
                  </Button>
                </td>
              </tr>
            ))}
          </tbody>
        </Table>
      ) : (
        <Text>No hi ha dispositius.</Text>
      )}

      <Title order={2} mt="xl">
        VERSIONS D'APLICACIÓ
      </Title>
      {versions && versions.length ? (
        <Table striped highlightOnHover mt="md">
          <thead>
            <tr>
              <th>Versió</th>
              <th>Nombre de dispositius</th>
            </tr>
          </thead>
          <tbody>
            {versions.map((v) => (
              <tr key={v.appVersion ?? 'unknown'}>
                <td>{v.appVersion ?? 'Desconeguda'}</td>
                <td>{v.count}</td>
              </tr>
            ))}
          </tbody>
        </Table>
      ) : (
        <Text>No hi ha dades de versions.</Text>
      )}
    </Container>
  );
}
