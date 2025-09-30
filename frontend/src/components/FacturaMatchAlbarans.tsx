import React, { useEffect, useState, useCallback } from "react";
import { Box, Title, Text, Paper, Group, Stack, Button } from "@mantine/core";
import { CLIENT_PLATFORM_HEADER, WEB_PLATFORM_VALUE } from "../api/client";

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

  console.log("🔧 Component FacturaMatchAlbarans carregat amb facturaId:", facturaId);
  console.log("🌍 VITE_API_BASE_URL:", import.meta.env.VITE_API_BASE_URL);
  console.log("🌐 URL hardcoded que usarem: https://validacio-backend.fly.dev/api");

  const fetchMatches = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const token = localStorage.getItem("token");
      console.log("🔗 Carregant matches per factura:", facturaId);
      const res = await fetch(`https://validacio-backend.fly.dev/api/factures/${facturaId}/auto-relate`, {
        headers: {
          Authorization: `Bearer ${token}`,
          Accept: "application/json",
          [CLIENT_PLATFORM_HEADER]: WEB_PLATFORM_VALUE,
        },
      });

      console.log("📡 Resposta matches:", res.status, res.statusText);
      if (!res.ok) throw new Error("Error carregant albarans relacionats");
      const data = await res.json();
      console.log("📦 Dades matches rebudes:", data);
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
  }, [facturaId]);

  const fetchCandidats = useCallback(async () => {
    try {
      const token = localStorage.getItem("token");
      console.log("🔍 Carregant candidats per factura:", facturaId);
      const res = await fetch(`https://validacio-backend.fly.dev/api/factures/${facturaId}/albarans-candidats`, {
        headers: {
          Authorization: `Bearer ${token}`,
          Accept: "application/json",
          [CLIENT_PLATFORM_HEADER]: WEB_PLATFORM_VALUE,
        },
      });

      console.log("📡 Resposta candidats:", res.status, res.statusText);
      if (!res.ok)
        throw new Error("No s'han pogut carregar els albarans candidats");
      const data = await res.json();
      console.log("📦 Dades candidats rebudes:", data);
      setCandidats(data);
    } catch (err) {
      console.error("❌ Error carregant candidats:", err);
      setError("Error carregant candidats: " + (err instanceof Error ? err.message : "Error desconegut"));
    }
  }, [facturaId]);

  const desvinculaAlbara = async (albaraId: number) => {
    setLoading(true);
    try {
      const token = localStorage.getItem("token");
      const res = await fetch(
        `https://validacio-backend.fly.dev/api/factures/${facturaId}/treure-albara/${albaraId}`,
        {
          method: "DELETE",
          headers: {
            Authorization: `Bearer ${token}`,
            [CLIENT_PLATFORM_HEADER]: WEB_PLATFORM_VALUE,
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

  useEffect(() => {
    fetchMatches();
  }, [fetchMatches]);

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
            📥 Tots els albarans del proveïdor disponibles ({candidats.length})
          </Title>
          <Text size="sm" c="dimmed" mb="md">
            Aquests són tots els albarans del proveïdor que encara no estan assignats a cap factura.
            Pots relacionar-los manualment si corresponen a aquesta factura.
          </Text>
          <Paper withBorder p="xs">
            <table style={{ width: "100%", borderCollapse: "collapse" }}>
              <thead>
                <tr style={{ backgroundColor: "#f0f0f0" }}>
                  <th style={{ padding: "8px", textAlign: "left" }}>
                    Referència
                  </th>
                  <th style={{ padding: "8px", textAlign: "left" }}>Import</th>
                  <th style={{ padding: "8px", textAlign: "left" }}>Coincidència</th>
                  <th style={{ padding: "8px", textAlign: "left" }}>Acció</th>
                </tr>
              </thead>
              <tbody>
                {candidats.map((a) => {
                  // Obtenim les referències de les línies de la factura per comparar
                  const referenciesFactura = resultats.flatMap(albara => 
                    albara.liniesFacturesRelacionades.map(linia => linia.referenciaDocumentDetall)
                  );
                  
                  // Comprovem tipus de coincidència
                  const coincideixReferencia = referenciesFactura.some(ref => 
                    ref.includes(a.referenciaDocument) || a.referenciaDocument.includes(ref)
                  );
                  
                  const importsFactura = resultats.flatMap(albara => 
                    albara.liniesFacturesRelacionades.map(linia => linia.importTotalDetall)
                  );
                  
                  const coincideixImport = importsFactura.some(imp => 
                    Math.abs(imp - a.importTotal) < 0.01
                  );
                  
                  let tipusCoincidencia = "❌ Cap";
                  let colorCoincidencia = "#666";
                  
                  if (coincideixReferencia && coincideixImport) {
                    tipusCoincidencia = "✅ Referència + Import";
                    colorCoincidencia = "green";
                  } else if (coincideixReferencia) {
                    tipusCoincidencia = "⚠️ Només referència";
                    colorCoincidencia = "orange";
                  } else if (coincideixImport) {
                    tipusCoincidencia = "⚠️ Només import";
                    colorCoincidencia = "orange";
                  }
                  
                  return (
                    <tr key={a.id}>
                      <td style={{ padding: "8px" }}>{a.referenciaDocument}</td>
                      <td style={{ padding: "8px" }}>
                        {a.importTotal.toFixed(2)} €
                      </td>
                      <td style={{ padding: "8px", color: colorCoincidencia, fontWeight: "bold" }}>
                        {tipusCoincidencia}
                      </td>
                      <td style={{ padding: "8px" }}>
                        <a
                          href={`/albarans/${a.id}/edit`}
                          target="_blank"
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
                  );
                })}
              </tbody>
            </table>
          </Paper>
        </Box>
      )}

      {candidats.length === 0 && !loading && (
        <Box mt="xl">
          <Text c="dimmed" ta="center" py="md">
            📭 No hi ha albarans disponibles del proveïdor per relacionar
          </Text>
        </Box>
      )}
    </Box>
  );
}
