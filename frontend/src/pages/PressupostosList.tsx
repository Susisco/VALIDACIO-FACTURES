// src/pages/PressupostosList.tsx
import React, { useState } from "react";
import { usePressupostos } from "../api/pressupostos";
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

export default function PressupostosList() {
  const navigate = useNavigate();
  const { data: pressupostos, isLoading, error } = usePressupostos();
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
  if (!pressupostos?.length)
    return (
      <Container py="xl">
        <Button
          onClick={() => navigate("/pressupostos/new")}
          style={{ marginBottom: 16 }}
        >
          Nou Pressupost
        </Button>
        <Text>No hi ha pressupostos.</Text>
      </Container>
    );

  const totalPages = Math.ceil(pressupostos.length / pageSize);
  const paginated = pressupostos.slice((page - 1) * pageSize, page * pageSize);

  return (
    <Container style={{ fontFamily: "Poppins, sans-serif" }} py="xl">
      <Title order={2}>PRESSUPOSTOS</Title>

      <Button
        onClick={() => navigate("/pressupostos/new")}
        style={{ marginBottom: 16 }}
      >
        Nou Pressupost
      </Button>
      <ScrollArea>
        <Table
          striped
          highlightOnHover
          style={{
            borderSpacing: "0 10px",
            borderCollapse: "separate",
          }}
        >
          <thead>
            <tr>
              <th>ID</th>
              <th>Referència</th>
              <th>Data</th>
              <th>Import</th>
              <th>Estat</th>
              <th>Proveïdor</th>
              <th>Factura</th>
              <th>Imatge</th>
              <th>Accions</th>
            </tr>
          </thead>
          <tbody>
            {paginated.map((p, index) => (
              <tr
                key={p.id}
                style={{
                  backgroundColor: index % 2 === 0 ? "#f9f9f9" : "#eaeaea",
                  borderRadius: "8px",
                }}
              >
                <td style={{ padding: "10px" }}>{p.id}</td>
                <td style={{ padding: "10px" }}>{p.referenciaDocument}</td>
                <td style={{ padding: "10px" }}>
                  {new Date(p.data).toLocaleDateString()}
                </td>
                <td style={{ padding: "10px" }}>{p.importTotal.toFixed(2)} €</td>
                <td style={{ padding: "10px" }}>{p.estat}</td>
                <td style={{ padding: "10px" }}>{p.proveidor?.nomComercial || "-"}</td>
                <td style={{ padding: "10px" }}>
                  {p.factura?.id ? (
                    <a
                      href={`/factures/${p.factura.id}`}
                      target="_blank"
                      rel="noopener noreferrer"
                      style={{ color: "#1a73e8", textDecoration: "underline" }}
                    >
                      #{p.factura.id}
                    </a>
                  ) : (
                    "-"
                  )}
                </td>
                <td style={{ padding: "10px" }}>
                  {p.fitxerAdjunt ? (
                    <a
                      href={`http://localhost:8080${p.fitxerAdjunt}`}
                      target="_blank"
                      rel="noopener noreferrer"
                      style={{ color: "#1a73e8", textDecoration: "underline" }}
                    >
                      Veure imatge
                    </a>
                  ) : (
                    "-"
                  )}
                </td>
                <td style={{ padding: "10px" }}>
                  <Button
                    variant="outline"
                    size="xs"
                    onClick={() => navigate(`/pressupostos/${p.id}/edit`)}
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
