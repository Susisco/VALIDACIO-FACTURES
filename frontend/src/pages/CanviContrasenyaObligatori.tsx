// src/pages/CanviContrasenyaObligatori.tsx
import React, { useState } from "react";
import {
  Container,
  Paper,
  Title,
  PasswordInput,
  Button,
  Alert,
} from "@mantine/core";
import { useNavigate } from "react-router-dom";
import { api } from "../api/client";

export default function CanviContrasenyaObligatori() {
  const [nova1, setNova1] = useState("");
  const [nova2, setNova2] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleSubmit = async () => {
    if (nova1 !== nova2) {
      setError("Les contrasenyes no coincideixen");
      return;
    }

    try {
      await api.post(
        "/auth/change-password",
        {
          oldPassword: "", // Estem en canvi obligatori, no demanem la vella
          newPassword: nova1,
        },
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        }
      );

      localStorage.setItem("contrasenyaTemporal", "false");
      navigate("/"); // o a on vulguis portar l’usuari
    } catch (err) {
      setError("Error canviant la contrasenya");
        console.error(err);
    }
  };

  return (
    <Container size="xs" mt={40}>
      <Paper p="lg" shadow="sm">
        <Title order={4} mb="md">
          Canvi obligatori de contrasenya
        </Title>
        <PasswordInput
          label="Nova contrasenya"
          value={nova1}
          onChange={(e) => setNova1(e.currentTarget.value)}
          required
        />
        <PasswordInput
          label="Repeteix la contrasenya"
          value={nova2}
          onChange={(e) => setNova2(e.currentTarget.value)}
          required
          mt="md"
        />
        {error && (
          <Alert color="red" mt="md">
            {error}
          </Alert>
        )}
        <Button fullWidth mt="lg" onClick={handleSubmit}>
          Canvia la contrasenya
        </Button>
      </Paper>
    </Container>
  );
}
