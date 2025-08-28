// src/pages/EdificisList.tsx
import React, { useState } from "react";
import { useEdificis } from "../api/edificis";
import { Container, Loader, Text, Table, Button, ScrollArea, Pagination, Title } from "@mantine/core";
import { useNavigate } from "react-router-dom";

export default function EdificisList() {
  const navigate = useNavigate();
  const { data: edificis = [], isLoading, error } = useEdificis() as { data: { id: number; nom: string; alias: string; ubicacio: string; }[]; isLoading: boolean; error: Error | null; };
  const [page, setPage] = useState(1);
  const pageSize = 20;

  if (isLoading) return (<Container py="xl" style={{ textAlign: "center" }}><Loader /></Container>);
  if (error) return (<Container py="xl"><Text color="red">Error: {error.message}</Text></Container>);
  if (!edificis?.length) return (<Container py="xl"><Text>No hi ha edificis.</Text></Container>);

  const totalPages = Math.ceil(edificis.length / pageSize);
  const paginated = edificis.slice((page - 1) * pageSize, page * pageSize);

  return (
    <Container style={{ fontFamily: 'Poppins, sans-serif' }} py="xl">
            <Title order={2}>EDIFICIS</Title>
      
      <Button onClick={() => navigate("/edificis/new")} style={{ marginBottom: 16 }}>
        Nou Edifici
      </Button>
      <ScrollArea>
        <Table striped highlightOnHover>
          <thead><tr><th>ID</th><th>Nom</th><th>Alias</th><th>Ubicació</th><th>Accions</th></tr></thead>
          <tbody>
            {paginated.map(e => (
              <tr key={e.id}>
                <td>{e.id}</td>
                <td>{e.nom}</td>
                <td>{e.alias}</td>
                <td>{e.ubicacio}</td>
                <td><Button variant="outline" size="xs" onClick={() => navigate(`/edificis/${e.id}/edit`)}>Editar</Button></td>
              </tr>
            ))}
          </tbody>
        </Table>
      </ScrollArea>
      <Pagination value={page} onChange={setPage} total={totalPages} mt="md" style={{ display: "flex", justifyContent: "center" }} />
    </Container>
  );
}
