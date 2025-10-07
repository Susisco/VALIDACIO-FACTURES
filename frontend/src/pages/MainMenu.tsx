import React from "react";
import { Button, Grid, Title, Tooltip } from "@mantine/core";
import { useNavigate } from "react-router-dom";

export default function MainMenu() {
  const navigate = useNavigate();
  const rol = localStorage.getItem("rol");

  // Definim permisos per rol
  const potVeureFactures = rol === "ADMINISTRADOR" || rol === "GESTOR";
  const potVeureAlbarans = rol === "ADMINISTRADOR" || rol === "GESTOR" || rol === "TREBALLADOR";
  const potVeureUsuaris = rol === "ADMINISTRADOR";
  const potVeureProveidors = rol === "ADMINISTRADOR" || rol === "GESTOR";
  const potGestionarDispositius = rol === "ADMINISTRADOR";

  // Estils comuns
  const buttonStyle = {
    width: "200px",
    backgroundColor: "#3498DB",
    color: "#fff",
    padding: "12px 24px",
    fontSize: "1rem",
    borderRadius: "8px",
  };

  const disabledButtonStyle = {
    ...buttonStyle,
    backgroundColor: "#ccc",
    color: "#666",
    cursor: "not-allowed",
  };

  return (
    <Grid gutter="md" justify="center">
      <Title
        order={1}
        style={{
          fontSize: "2.5rem",
          fontWeight: 700,
          color: "#2C3E50",
          fontFamily: "Poppins, sans-serif",
        }}
      >
        Menú Principal
      </Title>

      <Grid style={{ paddingLeft: "16px" }}>
        <Grid.Col span={6}>
          <Grid>
            <Grid.Col span={12} style={{ marginBottom: "16px" }}>
              <Tooltip
                label="Només accessible per a ADMINISTRADOR o GESTOR"
                disabled={potVeureFactures}
              >
                <Button
                  onClick={() => navigate("/factures/detall")}
                  style={potVeureFactures ? buttonStyle : disabledButtonStyle}
                  radius="md"
                  size="lg"
                  disabled={!potVeureFactures}
                >
                  FACTURES
                </Button>
              </Tooltip>
            </Grid.Col>

            <Grid.Col span={12} style={{ marginBottom: "16px" }}>
              <Tooltip label="Accés per a qualsevol rol" disabled={potVeureAlbarans}>
                <Button
                  onClick={() => navigate("/albarans")}
                  style={potVeureAlbarans ? buttonStyle : disabledButtonStyle}
                  radius="md"
                  size="lg"
                  disabled={!potVeureAlbarans}
                >
                  ALBARANS
                </Button>
              </Tooltip>
            </Grid.Col>
          </Grid>
        </Grid.Col>

        <Grid.Col span={6}>
          <Grid justify="center">
            <Grid.Col span={12} style={{ marginBottom: "16px", display: "flex", justifyContent: "center" }}>
              <Tooltip label="Només accessible per a ADMINISTRADOR" disabled={potVeureUsuaris}>
                <Button
                  onClick={() => navigate("/usuaris")}
                  style={potVeureUsuaris ? buttonStyle : disabledButtonStyle}
                  radius="md"
                  size="md"
                  disabled={!potVeureUsuaris}
                >
                  USUARIS
                </Button>
              </Tooltip>
            </Grid.Col>

            <Grid.Col span={12} style={{ marginBottom: "16px", display: "flex", justifyContent: "center" }}>
              <Tooltip
                label="Només accessible per a ADMINISTRADOR o GESTOR"
                disabled={potVeureProveidors}
              >
                <Button
                  onClick={() => navigate("/proveidors")}
                  style={potVeureProveidors ? buttonStyle : disabledButtonStyle}
                  radius="md"
                  size="md"
                  disabled={!potVeureProveidors}
                >
                  PROVEÏDORS
                </Button>
              </Tooltip>
            </Grid.Col>

            <Grid.Col span={12} style={{ marginBottom: "16px", display: "flex", justifyContent: "center" }}>
              <Tooltip
                label="Només accessible per a ADMINISTRADOR - Gestió de dispositius mòbils autoritzats"
                disabled={potGestionarDispositius}
              >
                <Button
                  onClick={() => navigate("/devices")}
                  style={potGestionarDispositius ? buttonStyle : disabledButtonStyle}
                  radius="md"
                  size="md"
                  disabled={!potGestionarDispositius}
                >
                  DISPOSITIUS
                </Button>
              </Tooltip>
            </Grid.Col>
          </Grid>
        </Grid.Col>
      </Grid>
    </Grid>
  );
}
