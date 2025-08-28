import React from "react";
import { Container, Title, Text, Button } from "@mantine/core";
import { useNavigate } from "react-router-dom";

export default function AccessDeniedPage() {
  const navigate = useNavigate();

  return (
    <Container size="sm" style={{ textAlign: "center", marginTop: "80px" }}>
      <Title order={2} mb="md">Accés denegat</Title>
      <Text color="dimmed" mb="lg">
        No tens permisos per accedir a aquesta pàgina.
      </Text>
      <Button variant="outline" onClick={() => navigate("/")}>
        Tornar a l'inici
      </Button>
    </Container>
  );
}
