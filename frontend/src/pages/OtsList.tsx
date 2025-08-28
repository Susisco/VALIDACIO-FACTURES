// src/pages/OtsList.tsx
import React, { useState } from "react";
import { useOts } from "../api/ots";
import { Container, Loader, Text, Table, Button, ScrollArea, Pagination, Title } from "@mantine/core";
import { useNavigate } from "react-router-dom";

export default function OtsList() {
  const navigate = useNavigate();
  interface OtsError {
    message: string;
  }

  const { data: ots = [], isLoading, error } = useOts() as { data: { id: number; codi: string; descripcio: string }[]; isLoading: boolean; error: OtsError };
  const [page, setPage] = useState(1);
  const pageSize = 20;

  if (isLoading) return (<Container py="xl" style={{ textAlign: "center" }}><Loader /></Container>);
  if (error) return (<Container py="xl"><Text color="red">Error: {error.message}</Text></Container>);
  if (!ots?.length) return (<Container py="xl"><Text>No hi ha OTS.</Text></Container>);

  const totalPages = Math.ceil(ots.length / pageSize);
  const paginated = ots.slice((page - 1) * pageSize, page * pageSize);

  return (
    <Container>
            <Title order={2}>ORDRES DE TREBALL</Title>
      
      <Button onClick={() => navigate("/ots/new")} style={{ marginBottom: 16 }}>
        Nova OTS
      </Button>
      <ScrollArea>
        <Table striped highlightOnHover>
          <thead><tr><th>ID</th><th>Codi</th><th>Descripció</th><th>Accions</th></tr></thead>
          <tbody>
            {paginated.map(o => (
              <tr key={o.id}>
                <td>{o.id}</td>
                <td>{o.codi}</td>
                <td>{o.descripcio}</td>
                <td><Button variant="outline" size="xs" onClick={() => navigate(`/ots/${o.id}/edit`)}>Editar</Button></td>
              </tr>
            ))}
          </tbody>
        </Table>
      </ScrollArea>
      <Pagination value={page} onChange={setPage} total={totalPages} mt="md" style={{ display: "flex", justifyContent: "center" }} />
    </Container>
  );
}
