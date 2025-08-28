// src/pages/ProveidorsList.tsx
import React, { useState } from "react";
import { useProveidors } from "../api/proveidors";
import {
  Container,
  Loader,
  Text,
  Table,
  Button,
  ScrollArea,
  Pagination,
  Title,
} from "@mantine/core";
import { useNavigate } from "react-router-dom";

export default function ProveidorsList() {
  const navigate = useNavigate();
  const { data: proveidors, isLoading, error } = useProveidors();
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
  if (!proveidors?.length)
    return (
      <Container py="xl">
        <Text>No hi ha proveidors.</Text>
      </Container>
    );

  const totalPages = Math.ceil(proveidors.length / pageSize);
  const paginated = proveidors.slice((page - 1) * pageSize, page * pageSize);

  return (
    <Container style={{ fontFamily: "Poppins, sans-serif" }} py="xl">
      <Title order={2}>PROVEÏDORS</Title>

      <Button
        onClick={() => navigate("/proveidors/new")}
        style={{ marginBottom: 16 }}
      >
        Nou Proveïdor
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
              <th>Comercial</th>
              <th>Nom</th>
              <th>NIF</th>
              <th>Adreça</th>
              <th>Accions</th>
            </tr>
          </thead>
          <tbody>
            {paginated.map((p, index) => (
              <tr
                key={p.id}
                style={{
                  backgroundColor: index % 2 === 0 ? "#f9f9f9" : "#eaeaea", // Alternar colores
                  borderRadius: "8px", // Bordes redondeados
                }}
              >
                <td style={{ padding: "10px" }}>{p.id}</td>
                <td style={{ padding: "10px" }}>{p.nomComercial}</td>
                <td style={{ padding: "10px" }}>{p.nom}</td>
                <td style={{ padding: "10px" }}>{p.nif}</td>
                <td style={{ padding: "10px" }}>{p.adreca}</td>
                <td style={{ padding: "10px" }}>
                  <Button
                    variant="outline"
                    size="xs"
                    onClick={() => navigate(`/proveidors/${p.id}/edit`)}
                  >
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
        style={{ justifyContent: "center", display: "flex" }}
        mt="md"
      />
    </Container>
  );
}