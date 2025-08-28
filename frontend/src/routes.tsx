// src/routes.tsx
import React, { Suspense, lazy } from "react";
import Layout from "./pages/Layout";
import { Outlet, RouteObject, useRoutes } from "react-router-dom";
import RutaProtegidaPerRol from "./components/RutaProtegidaPerRol";
import LoginPage from "./pages/LoginPage";
import CanviContrasenyaObligatori from "./pages/CanviContrasenyaObligatori";

//MAIN MENU
import MainMenu from "./pages/MainMenu";

//FACTURES
const FacturesListDetall = lazy(() => import("./pages/FacturesListDetall"));
const FacturaFormNew = lazy(() => import("./pages/FacturaFormNew"));
const FacturaFormEdit = lazy(() => import("./pages/FacturaFormEdit"));

//ALBARANS
const AlbaransList = lazy(() => import("./pages/AlbaransList"));
const AlbaraFormNew = lazy(() => import("./pages/AlbaraFormNew"));
const AlbaraFormEdit = lazy(() => import("./pages/AlbaraFormEdit"));

//EDIFICIS
const EdificisList = lazy(() => import("./pages/EdificisList"));

//OTS
const OtsList = lazy(() => import("./pages/OtsList"));

//USUARIS
const UsuarisList = lazy(() => import("./pages/UsuarisList"));
const UsuariFormNew = lazy(() => import("./pages/UsuariFormNew"));
const UsuariFormEdit = lazy(() => import("./pages/UsuariFormEdit"));

//PRESSUPOSTOS
const PressupostosList = lazy(() => import("./pages/PressupostosList"));
const PressupostFormNew = lazy(() => import("./pages/PressupostFormNew"));
const PressupostFormEdit = lazy(() => import("./pages/PressupostFormEdit"));

//PROVEIDORS
const ProveidorsList = lazy(() => import("./pages/ProveidorsList"));
const ProveidorFormNew = lazy(() => import("./pages/ProveidorFormNew"));
const ProveidorFormEdit = lazy(() => import("./pages/ProveidorFormEdit"));

//DEVICES
const DeviceAdminPage = lazy(() => import("./pages/DeviceAdminPage"));

const routesConfig: RouteObject[] = [
  {
    path: "/login",
    element: (
      <Layout>
        <LoginPage />
      </Layout>
    ),
  },
  {
    path: "/canvi-contrasenya-obligatori",
    element: (
      <Layout>
        <CanviContrasenyaObligatori />
      </Layout>
    ),
  },
  {
    path: "/",
    element: (
      <Layout>
        <Outlet />
      </Layout>
    ),
    children: [
      { index: true, element: <MainMenu /> },

      // ALBARANS (tots els rols)
      {
        path: "albarans",
        element: (
          <RutaProtegidaPerRol rolsPermesos={["ADMINISTRADOR", "GESTOR", "TREBALLADOR"]}>
            <AlbaransList />
          </RutaProtegidaPerRol>
        ),
      },
      {
        path: "albarans/new",
        element: (
          <RutaProtegidaPerRol rolsPermesos={["ADMINISTRADOR", "GESTOR", "TREBALLADOR"]}>
            <AlbaraFormNew />
          </RutaProtegidaPerRol>
        ),
      },
      {
        path: "albarans/:id/edit",
        element: (
          <RutaProtegidaPerRol rolsPermesos={["ADMINISTRADOR", "GESTOR", "TREBALLADOR"]}>
            <AlbaraFormEdit />
          </RutaProtegidaPerRol>
        ),
      },

      // FACTURES (admin i gestor)

      {
        path: "factures/detall",
        element: (
          <RutaProtegidaPerRol rolsPermesos={["ADMINISTRADOR", "GESTOR"]}>
            <FacturesListDetall />
          </RutaProtegidaPerRol>
        ),
      },
      {
        path: "factures/new",
        element: (
          <RutaProtegidaPerRol rolsPermesos={["ADMINISTRADOR", "GESTOR"]}>
            <FacturaFormNew />
          </RutaProtegidaPerRol>
        ),
      },
      {
        path: "factures/:id",
        element: (
          <RutaProtegidaPerRol rolsPermesos={["ADMINISTRADOR", "GESTOR"]}>
            <FacturaFormEdit />
          </RutaProtegidaPerRol>
        ),
      },      
      {
        path: "factures/:id/factura",
        element: (
          <RutaProtegidaPerRol rolsPermesos={["ADMINISTRADOR", "GESTOR"]}>
            <FacturaFormEdit />
          </RutaProtegidaPerRol>
        ),
      },

      // PRESSUPOSTOS (admin i gestor)
      {
        path: "pressupostos",
        element: (
          <RutaProtegidaPerRol rolsPermesos={["ADMINISTRADOR", "GESTOR"]}>
            <PressupostosList />
          </RutaProtegidaPerRol>
        ),
      },
      {
        path: "pressupostos/new",
        element: (
          <RutaProtegidaPerRol rolsPermesos={["ADMINISTRADOR", "GESTOR"]}>
            <PressupostFormNew />
          </RutaProtegidaPerRol>
        ),
      },
      {
        path: "pressupostos/:id/edit",
        element: (
          <RutaProtegidaPerRol rolsPermesos={["ADMINISTRADOR", "GESTOR"]}>
            <PressupostFormEdit />
          </RutaProtegidaPerRol>
        ),
      },

      // PROVEIDORS (admin i gestor)
      {
        path: "proveidors",
        element: (
          <RutaProtegidaPerRol rolsPermesos={["ADMINISTRADOR", "GESTOR"]}>
            <ProveidorsList />
          </RutaProtegidaPerRol>
        ),
      },
      {
        path: "proveidors/new",
        element: (
          <RutaProtegidaPerRol rolsPermesos={["ADMINISTRADOR", "GESTOR"]}>
            <ProveidorFormNew />
          </RutaProtegidaPerRol>
        ),
      },
      {
        path: "proveidors/:id/edit",
        element: (
          <RutaProtegidaPerRol rolsPermesos={["ADMINISTRADOR", "GESTOR"]}>
            <ProveidorFormEdit />
          </RutaProtegidaPerRol>
        ),
      },

      // USUARIS (només admin)
      {
        path: "usuaris",
        element: (
          <RutaProtegidaPerRol rolsPermesos={["ADMINISTRADOR"]}>
            <UsuarisList />
          </RutaProtegidaPerRol>
        ),
      },
      {
        path: "usuaris/new",
        element: (
          <RutaProtegidaPerRol rolsPermesos={["ADMINISTRADOR"]}>
            <UsuariFormNew />
          </RutaProtegidaPerRol>
        ),
      },
      {
        path: "usuaris/:id/edit",
        element: (
          <RutaProtegidaPerRol rolsPermesos={["ADMINISTRADOR"]}>
            <UsuariFormEdit />
          </RutaProtegidaPerRol>
        ),
      },

      // OTS i EDIFICIS (admin i gestor)
      {
        path: "ots",
        element: (
          <RutaProtegidaPerRol rolsPermesos={["ADMINISTRADOR", "GESTOR"]}>
            <OtsList />
          </RutaProtegidaPerRol>
        ),
      },
      {
        path: "edificis",
        element: (
          <RutaProtegidaPerRol rolsPermesos={["ADMINISTRADOR", "GESTOR"]}>
            <EdificisList />
          </RutaProtegidaPerRol>
        ),
      },

      // DEVICES (només admin)
      {
        path: "devices",
        element: (
          <RutaProtegidaPerRol rolsPermesos={["ADMINISTRADOR"]}>
            <DeviceAdminPage />
          </RutaProtegidaPerRol>
        ),
      },
    ],
  },
];

// El componente RoutesProvider utiliza el hook useRoutes de react-router-dom para crear las rutas
// a partir de la configuración definida anteriormente. Este componente se encarga de renderizar
export default function RoutesProvider() {
  const element = useRoutes(routesConfig);
  return (
    <Suspense fallback={<div style={{ textAlign: "center", marginTop: 50 }}>Loading...</div>}>
      {element}
    </Suspense>
  );
}
