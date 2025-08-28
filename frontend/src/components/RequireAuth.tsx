// src/components/RequireAuth.tsx
import React from "react";
import { Navigate, Outlet, useLocation } from "react-router-dom";


export default function RequireAuth() {
  const token = localStorage.getItem("token");   // cap useState
  const location = useLocation();
  return token
    ? <Outlet />
    : <Navigate to="/login" state={{ from: location }} replace />;
}
