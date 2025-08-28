import React, { useState } from "react";
import {
  Container,
  TextInput,
  PasswordInput,
  Button,
  Title,
  Paper,
  Alert,
} from "@mantine/core";
import { useNavigate, useLocation } from "react-router-dom";
import { api } from "../api/client";

//LoginPage es la pàgina d'inici de sessió del sistema
export default function LoginPage() {
  const [email, setEmail] = useState("");
  const [contrasenya, setContrasenya] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();
  const location = useLocation();

  interface LocationState {
    from?: {
      pathname?: string;
    };
  }

  const state = location.state as LocationState;
  const from = state?.from?.pathname || "/";


  //intenta iniciar sessió enviant les credencials a l'API
  //si té contrasenya temporal, redirigeix a la pàgina de canvi de contrasenya obligatori
  //si tot va bé, redirigeix a la pàgina d'inici o a la pàgina anterior
  const handleLogin = async () => {
    try {
          console.log("[login] intent", { email });

      const response = await api.post("/auth/login", { email, contrasenya });

          console.log("[login] response", response.status, response.data);

      const { token, nom, id, contrasenyaTemporal,rol } = response.data;

      localStorage.setItem("token", token);
      localStorage.setItem("nom", nom);
      localStorage.setItem("usuariId", id.toString());
      localStorage.setItem(
        "contrasenyaTemporal",
        contrasenyaTemporal.toString()
      );
      localStorage.setItem("rol", rol);

      if (contrasenyaTemporal) {
              console.log("[login] password temporal → redirect canvi");

        navigate("/canvi-contrasenya-obligatori");
        return;
      }
    console.log("[login] ok → redirect", { to: from });

      navigate(from, { replace: true });
    } catch (err: any) {
    const status = err?.response?.status;
    const data = err?.response?.data;
    console.error("[login] error", { status, data });
    setError(status === 401 ? "Credencials incorrectes" : "Error de connexió");
  }
  };

  return (
    <Container
      size={420}
      my={40}
      style={{
        maxWidth: 1000,
        margin: "auto",
        fontFamily: "Poppins, sans-serif",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
      }}
    >
      <Paper
        withBorder
        shadow="md"
        p={30}
        radius="md"
        style={{ width: "100%", maxWidth: 420 }}
      >
        <Title style={{ textAlign: "center" }}>Accés al sistema</Title>
        <TextInput
          label="Email"
          placeholder="teu.email@exemple.cat"
          required
          value={email}
          onChange={(event) => setEmail(event.currentTarget.value)}
        />
        <PasswordInput
          label="Contrasenya"
          placeholder="••••••••"
          required
          mt="md"
          value={contrasenya}
          onChange={(event) => setContrasenya(event.currentTarget.value)}
        />
        {error && (
          <Alert color="red" mt="md">
            {error}
          </Alert>
        )}
        <Button fullWidth mt="xl" onClick={handleLogin}>
          Inicia sessió, PROVA sisco
        </Button>
      </Paper>
    </Container>
  );
}
