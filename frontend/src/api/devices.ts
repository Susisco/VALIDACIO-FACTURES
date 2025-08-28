import { useQuery } from '@tanstack/react-query';
import { api } from './client';

export interface DeviceVersionCount {
  appVersion: string | null;
  count: number;
}

export const getDeviceVersions = (): Promise<DeviceVersionCount[]> =>
  api.get('/devices/versions').then((r) => r.data);

export const useDeviceVersions = () =>
  useQuery({ queryKey: ['deviceVersions'], queryFn: getDeviceVersions });

