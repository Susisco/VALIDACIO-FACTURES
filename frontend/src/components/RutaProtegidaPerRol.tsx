// src/components/RutaProtegidaPerRol.tsx
import React from "react";
import { Navigate } from "react-router-dom";
import AccessDeniedPage from "../pages/AccessDeniedPage";

interface Props {
  rolsPermesos: string[];
  children: React.ReactNode;
}

export default function RutaProtegidaPerRol({ rolsPermesos, children }: Props) {
  const rol = localStorage.getItem("rol");

  if (!rol || !rolsPermesos.includes(rol)) {
    return <AccessDeniedPage />;
  }

  return <>{children}</>;
}
