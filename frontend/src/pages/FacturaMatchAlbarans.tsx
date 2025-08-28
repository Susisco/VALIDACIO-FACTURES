import React, { useEffect, useState, useCallback } from "react";
import { Box, Title, Text, Paper, Group, Stack, Button } from "@mantine/core";

interface DetallMatchDTO {
  detallId: number;
  referenciaDocumentDetall: string;
  importTotalDetall: number;
  albaraRelacionatId?: number;
}

interface AlbaraMatchDTO {
  albaraId: number;
  referenciaAlbara: string;
  importTotal: number;
  liniesFacturesRelacionades: DetallMatchDTO[];
}

interface AlbaraCandidat {
  id: number;
  referenciaDocument: string;
  importTotal: number;
}

interface Props {
  facturaId: number;
}

export default function FacturaMatchAlbarans({ facturaId }: Props) {
  const [resultats, setResultats] = useState<AlbaraMatchDTO[]>([]);
  const [candidats, setCandidats] = useState<AlbaraCandidat[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchMatches = async () => {
    setLoading(true);
    setError(null);
    try {
      const token = localStorage.getItem("token");
      const res = await fetch(`/api/factures/${facturaId}/auto-relate`, {
        headers: {
          Authorization: `Bearer ${token}`,
          Accept: "application/json",
        },
      });

      if (!res.ok) throw new Error("Error carregant albarans relacionats");
      const data = await res.json();
      setResultats(data.albaransAutoRelats);
    } catch (err) {
      if (err instanceof Error) {
        setError(err.message);
      } else {
        setError("Error desconegut");
      }
    } finally {
      setLoading(false);
    }
  };

  const fetchCandidats = useCallback(async () => {
    try {
      const token = localStorage.getItem("token");
      const res = await fetch(`/api/factures/${facturaId}/albarans-candidats`, {
        headers: {
          Authorization: `Bearer ${token}`,
          Accept: "application/json",
        },
      });

      if (!res.ok)
        throw new Error("No s'han pogut carregar els albarans candidats");
      const data = await res.json();
      setCandidats(data);
    } catch (err) {
      console.error(err);
    }
  }, [facturaId]);

  const desvinculaAlbara = async (albaraId: number) => {
    setLoading(true);
    try {
      const token = localStorage.getItem("token");
      const res = await fetch(
        `/api/factures/${facturaId}/treure-albara/${albaraId}`,
        {
          method: "DELETE",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (!res.ok) throw new Error("No s'ha pogut desvincular l'albarà");
      await fetchMatches();
      await fetchCandidats();
    } catch (err) {
      console.error("Error desvinculant:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCandidats();
  }, [fetchCandidats]);

  return (
    <Box mt="md">
      <Group justify="space-between" mb="sm">
        <Title order={4}>🔗 Albarans relacionats automàticament</Title>
        <Button variant="light" onClick={fetchMatches} loading={loading}>
          Torna a fer match
        </Button>
      </Group>

      {error && <Text c="red">❌ {error}</Text>}

      {resultats.length === 0 && !loading && (
        <Text>No s'han trobat coincidències.</Text>
      )}

      <Stack mt="sm" gap="md">
        {resultats.map((albara: AlbaraMatchDTO) => (
          <Paper key={albara.albaraId} withBorder p="xs">
            <table style={{ width: "100%", borderCollapse: "collapse" }}>
              <thead>
                <tr style={{ backgroundColor: "#f0f0f0" }}>
                                    <th style={{ padding: "8px", textAlign: "left" }}>
                    Línies Factura
                  </th>
                  <th style={{ padding: "8px", textAlign: "left" }}>
                    Referència
                  </th>
                  <th style={{ padding: "8px", textAlign: "left" }}>Import</th>

                  <th style={{ padding: "8px", textAlign: "left" }}>Acció</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                                    <td style={{ padding: "8px" }}>
                    <ul style={{ margin: 0, paddingLeft: 16 }}>
                      {albara.liniesFacturesRelacionades.map(
                        (detall: DetallMatchDTO) => (
                          <li key={detall.detallId}>
                            {detall.referenciaDocumentDetall} —{" "}
                            {detall.importTotalDetall.toFixed(2)} €
                          </li>
                        )
                      )}
                    </ul>
                  </td>
                  <td style={{ padding: "8px" }}>{albara.referenciaAlbara}</td>
                  <td style={{ padding: "8px" }}>
                    <span
                      style={{
                        color:
                          albara.importTotal !==
                          albara.liniesFacturesRelacionades.reduce(
                            (sum, detall) => sum + detall.importTotalDetall,
                            0
                          )
                            ? "red"
                            : "inherit", // Rojo si no coincide, color por defecto si coincide
                      }}
                    >
                      {albara.importTotal.toFixed(2)} €
                    </span>
                  </td>

                  <td style={{ padding: "8px" }}>
                    <Button
                      color="blue"
                      size="xs"
                      variant="outline"
                      component="a"
                      href={`/albarans/${albara.albaraId}/edit`}
                      target="_blank" // Abre en una nueva ventana
                      rel="noopener noreferrer" // Mejora la seguridad
                      style={{ marginRight: "8px" }} // Espacio entre botones
                    >
                      ✏️ Edita
                    </Button>
                  </td>
                </tr>
              </tbody>
            </table>
          </Paper>
        ))}
      </Stack>

      {candidats.length > 0 && (
        <Box mt="xl">
          <Title order={5} mb="sm">
            📥 Albarans disponibles per editar
          </Title>
          <Paper withBorder p="xs">
            <table style={{ width: "100%", borderCollapse: "collapse" }}>
              <thead>
                <tr style={{ backgroundColor: "#f0f0f0" }}>
                  <th style={{ padding: "8px", textAlign: "left" }}>
                    Referència
                  </th>
                  <th style={{ padding: "8px", textAlign: "left" }}>Import</th>
                  <th style={{ padding: "8px", textAlign: "left" }}>Acció</th>
                </tr>
              </thead>
              <tbody>
                {candidats.map((a) => (
                  <tr key={a.id}>
                    <td style={{ padding: "8px" }}>{a.referenciaDocument}</td>
                    <td style={{ padding: "8px" }}>
                      {a.importTotal.toFixed(2)} €
                    </td>
                    <td style={{ padding: "8px" }}>
                      <a
                        href={`/albarans/${a.id}/edit`}
                        target="_blank" // Abre en una nueva ventana
                        style={{
                          color: "#007BFF",
                          textDecoration: "none",
                          fontSize: "14px",
                        }}
                      >
                        ✏️ Edita
                      </a>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </Paper>
        </Box>
      )}
    </Box>
  );
}
