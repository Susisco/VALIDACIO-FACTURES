import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { api } from './client';

export type DeviceStatus = 'PENDING' | 'APPROVED' | 'REVOKED';

export interface Device {
  id: number;
  fid: string;
  userId: number | null;
  status: DeviceStatus;
}

export const getDevices = (): Promise<Device[]> =>
  api.get('/admin/devices').then((r) => r.data);

export const approveDevice = (fid: string): Promise<void> =>
  api.post(`/admin/devices/${fid}/approve`).then((r) => r.data);

export const revokeDevice = (fid: string): Promise<void> =>
  api.post(`/admin/devices/${fid}/revoke`).then((r) => r.data);

export const useDevices = () =>
  useQuery({ queryKey: ['devices'], queryFn: getDevices });

export const useApproveDevice = () => {
  const qc = useQueryClient();
  return useMutation<void, Error, string>({
    mutationFn: approveDevice,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['devices'] }),
  });
};

export const useRevokeDevice = () => {
  const qc = useQueryClient();
  return useMutation<void, Error, string>({
    mutationFn: revokeDevice,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['devices'] }),
  });
};

export interface DeviceVersionCount {
  appVersion: string | null;
  count: number;
}

export const getDeviceVersions = (): Promise<DeviceVersionCount[]> =>
  api.get('/devices/versions').then((r) => r.data);

export const useDeviceVersions = () =>
  useQuery({ queryKey: ['deviceVersions'], queryFn: getDeviceVersions });
