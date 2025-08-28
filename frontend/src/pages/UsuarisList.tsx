import React, { useState } from "react";
import { useUsuaris } from "../api/usuaris";
import { Container, Loader, Text, Table, Button, ScrollArea, Pagination, Title } from "@mantine/core";
import { useNavigate } from "react-router-dom";

export default function UsuarisList() {
  const navigate = useNavigate();
  const { data: usuaris, isLoading, error } = useUsuaris() as {
    data: { id: number; nom: string; email: string; rol: string }[] | undefined;
    isLoading: boolean;
    error: { message: string } | null;
  };
  const [page, setPage] = useState(1);
  const pageSize = 20;

  if (isLoading)
    return (
      <Container py="xl" style={{ textAlign: "center" }}>
        <Loader />
      </Container>
    );
  if (error)
    return (
      <Container py="xl">
        <Text color="red">Error: {error.message}</Text>
      </Container>
    );
  if (!usuaris?.length)
    return (
      <Container py="xl">
        <Text>No hi ha usuaris.</Text>
      </Container>
    );

  const totalPages = Math.ceil(usuaris.length / pageSize);
  const paginated = usuaris.slice((page - 1) * pageSize, page * pageSize);

  return (
    <Container style={{ fontFamily: "Poppins, sans-serif" }} py="xl">
      <Title order={2}>USUARIS</Title>

      <Button onClick={() => navigate("/usuaris/new")} style={{ marginBottom: 16 }}>
        Nou Usuari
      </Button>
      <ScrollArea>
        <Table
          striped
          highlightOnHover
          style={{
            borderSpacing: "0 10px", // Espaciado entre filas
            borderCollapse: "separate", // Asegura que el espaciado funcione
          }}
        >
          <thead>
            <tr>
              <th>ID</th>
              <th>Nom</th>
              <th>Email</th>
              <th>Rol</th>
              <th>Accions</th>
            </tr>
          </thead>
          <tbody>
            {paginated.map((u, index) => (
              <tr
                key={u.id}
                style={{
                  backgroundColor: index % 2 === 0 ? "#f9f9f9" : "#eaeaea", // Alternar colores
                  borderRadius: "8px", // Bordes redondeados
                }}
              >
                <td style={{ padding: "10px" }}>{u.id}</td>
                <td style={{ padding: "10px" }}>{u.nom}</td>
                <td style={{ padding: "10px" }}>{u.email}</td>
                <td style={{ padding: "10px" }}>{u.rol}</td>
                <td style={{ padding: "10px" }}>
                  <Button variant="outline" size="xs" onClick={() => navigate(`/usuaris/${u.id}/edit`)}>
                    Editar
                  </Button>
                </td>
              </tr>
            ))}
          </tbody>
        </Table>
      </ScrollArea>
      <Pagination
        value={page}
        onChange={setPage}
        total={totalPages}
        mt="md"
        style={{ display: "flex", justifyContent: "center" }}
      />
    </Container>
  );
}