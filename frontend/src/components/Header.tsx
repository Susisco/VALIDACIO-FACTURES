// src/components/Header.tsx
import React from "react";
import { Title, ActionIcon, Tooltip, Anchor } from "@mantine/core";
import { Home, ArrowLeft, Logout, Login } from "tabler-icons-react";
import { Link, useNavigate } from "react-router-dom";

export default function AppHeader() {
  const navigate = useNavigate();
  const token = localStorage.getItem("token");
  const nom = localStorage.getItem("nom");


const handleLogout = () => {
  localStorage.removeItem("token");
  localStorage.removeItem("nom");
  navigate("/login"); // redirigeix a login
};


  return (
    <header
      style={{
        display: "flex",
        alignItems: "center",
        justifyContent: "space-between",
        background: "#E3F2FD",
        padding: "0 16px",
        margin: "0 auto",
        maxWidth: "98%",
        width: "95%",
        minHeight: 60,
        height: 60,
        boxShadow: "0 2px 4px rgba(0, 0, 0, 0.1)",
        borderBottom: "1px solid rgba(0, 0, 0, 0.1)",
        borderRadius: "0 0 8px 8px",
        position: "fixed",
        zIndex: 10,
      }}
    >
      {/* Esquerra: Home i Enrere */}
      <div
        style={{
          flex: 1,
          display: "flex",
          justifyContent: "flex-start",
          gap: "8px",
        }}
      >
        <ActionIcon
          size="lg"
          variant="light"
          component={Link}
          to="/"
          style={iconStyle}
        >
          <Home size={40} strokeWidth={1.5} />
        </ActionIcon>

        <ActionIcon
          size="lg"
          variant="light"
          onClick={() => navigate(-1)}
          style={iconStyle}
        >
          <ArrowLeft size={40} strokeWidth={1.5} />
        </ActionIcon>
      </div>

      {/* Centre: Títol */}
      <div style={{ flex: 2, display: "flex", justifyContent: "center" }}>
        <Title
          order={1}
          style={{
            margin: 1,
            fontFamily: "Poppins, sans-serif",
            fontWeight: 600,
            color: "#1976D2",
          }}
        >
          Gestió de Factures
        </Title>
      </div>

      {/* Dreta: Login o Logout */}
      <div style={{ flex: 1, display: "flex", justifyContent: "flex-end", alignItems: "center", gap: "16px" }}>
        <Anchor component={Link} to="/politica-privadesa" size="sm" c="blue.7" style={{ fontWeight: 500 }}>
          Política de privadesa
        </Anchor>
        {/* Mostra el nom si està definit */}
        {nom && (
          <span style={{ fontWeight: 500, fontFamily: "Poppins, sans-serif", color: "#1976D2" }}>
            Usuari: {nom}
          </span>
        )}
        {token ? (
          <Tooltip label="Tanca la sessió">
            <ActionIcon
              size="lg"
              variant="light"
              onClick={handleLogout}
              style={iconStyle}
            >
              <Logout size={40} strokeWidth={1.5} />
            </ActionIcon>
          </Tooltip>
        ) : (
          <Tooltip label="Inicia sessió">
            <ActionIcon
              size="lg"
              variant="light"
              onClick={() => navigate("/login")}
              style={iconStyle}
            >
              <Login size={40} strokeWidth={1.5} />
            </ActionIcon>
          </Tooltip>
        )}
      </div>
    </header>
  );
}

const iconStyle = {
  display: "flex",
  alignItems: "center",
  justifyContent: "center",
  backgroundColor: "#E3F2FD",
  color: "#1976D2",
  borderRadius: "50%",
  boxShadow: "0 2px 4px rgba(0, 0, 0, 0.1)",
  border: "none",
};
