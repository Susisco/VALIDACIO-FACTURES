import React, { useMemo, useState } from "react";
import {
  Container,
  Loader,
  Text,
  Table,
  Button,
  ScrollArea,
  Pagination,
  Title,
  TextInput,
  Group,
  Flex,
} from "@mantine/core";
import { IconChevronUp, IconChevronDown } from "@tabler/icons-react";
import { useNavigate } from "react-router-dom";
import { useAlbarans } from "../api/albarans";
import { API_BASE_URL } from "../config/constants";

// Minimal typing per Albarà
interface Albara {
  id: number;
  referenciaDocument: string;
  data: string;
  importTotal: number;
  estat: string;
  proveidor?: { nom: string };
  factura?: { id: number };
  fitxerAdjunt?: string | null;
  creador?: { id: number };
}

type SortKey =
  | "id"
  | "referenciaDocument"
  | "data"
  | "importTotal"
  | "estat"
  | "proveidor"
  | "factura";

type SortConfig = {
  key: SortKey;
  direction: "asc" | "desc";
} | null;

import { useMantineTheme } from "@mantine/core";

export default function AlbaransList() {
  const theme = useMantineTheme();
  const navigate = useNavigate();
  const { data: albarans, isLoading, error } = useAlbarans();
  const [page, setPage] = useState(1);
  const pageSize = 20;

  const rol = localStorage.getItem("rol");
  const usuariId = Number(localStorage.getItem("usuariId"));

  // Filtrar per rol
  const albaransFiltratsRol = useMemo(() => {
    if (rol === "TREBALLADOR") {
      return (albarans || []).filter((a) => a.creador?.id === usuariId);
    }
    return albarans || [];
  }, [albarans, rol, usuariId]);

  // Estats dels filtres
  const [filterId, setFilterId] = useState("");
  const [filterRef, setFilterRef] = useState("");
  const [filterEstat, setFilterEstat] = useState("");
  const [filterProv, setFilterProv] = useState("");
  const [filterFactura, setFilterFactura] = useState("");
  const [dateFrom, setDateFrom] = useState("");
  const [dateTo, setDateTo] = useState("");
  const [importMin, setImportMin] = useState("");
  const [importMax, setImportMax] = useState("");
  const [filterImage, setFilterImage] = useState<"all" | "with" | "without">(
    "all"
  );
  const [sortConfig, setSortConfig] = useState<SortConfig>(null);

  // Funció per ordenar i filtrar
  const sortedAlbarans = useMemo(() => {
    let lista = [...albaransFiltratsRol];
    // Aplicar filtres
    lista = lista.filter((a) => {
      if (filterId && a.id.toString() !== filterId) return false; // Compara el ID del albarán con el filtro
      if (
        filterRef &&
        !a.referenciaDocument.toLowerCase().includes(filterRef.toLowerCase())
      )
        return false;
      if (
        filterEstat &&
        a.estat.toLowerCase().indexOf(filterEstat.toLowerCase()) === -1
      )
        return false;
      if (
        filterProv &&
        a.proveidor?.nom.toLowerCase().indexOf(filterProv.toLowerCase()) === -1
      )
        return false;
      if (filterFactura) {
        const fId = a.factura?.id.toString() || "";
        if (!fId.includes(filterFactura)) return false; // Cambiado para permitir coincidencias parciales
      }
      if (dateFrom && new Date(a.data) < new Date(dateFrom)) return false;
      if (dateTo && new Date(a.data) > new Date(dateTo)) return false;
      const imp = a.importTotal;
      if (importMin && imp < parseFloat(importMin)) return false;
      if (importMax && imp > parseFloat(importMax)) return false;
      if (filterImage === "with" && !a.fitxerAdjunt) return false;
      if (filterImage === "without" && a.fitxerAdjunt) return false;
      return true;
    });
    // Ordenar
    if (sortConfig !== null) {
      lista.sort((a, b) => {
        let aVal: string | number = a[sortConfig.key as keyof Albara] as
          | string
          | number;
        let bVal: string | number = b[sortConfig.key as keyof Albara] as
          | string
          | number;
        if (sortConfig.key === "proveidor") {
          aVal = a.proveidor?.nom || "";
          bVal = b.proveidor?.nom || "";
        }
        if (sortConfig.key === "factura") {
          aVal = a.factura?.id || 0;
          bVal = b.factura?.id || 0;
        }
        if (sortConfig.key === "data") {
          aVal = new Date(a.data).getTime();
          bVal = new Date(b.data).getTime();
        }
        if (typeof aVal === "string") aVal = aVal.toLowerCase();
        if (typeof bVal === "string") bVal = bVal.toLowerCase();
        if (aVal < bVal) return sortConfig.direction === "asc" ? -1 : 1;
        if (aVal > bVal) return sortConfig.direction === "asc" ? 1 : -1;
        return 0;
      });
    }
    return lista;
  }, [
    albaransFiltratsRol,
    filterId,
    filterRef,
    filterEstat,
    filterProv,
    filterFactura,
    dateFrom,
    dateTo,
    importMin,
    importMax,
    filterImage,
    sortConfig,
  ]);

  const totalPages = Math.ceil(sortedAlbarans.length / pageSize);
  const paginated = sortedAlbarans.slice(
    (page - 1) * pageSize,
    page * pageSize
  );

  // Manejador clic a capçalera
  const requestSort = (key: SortKey) => {
    let direction: "asc" | "desc" = "asc";
    if (
      sortConfig &&
      sortConfig.key === key &&
      sortConfig.direction === "asc"
    ) {
      direction = "desc";
    }
    if (
      sortConfig &&
      sortConfig.key === key &&
      sortConfig.direction === "desc"
    ) {
      setSortConfig(null);
      return;
    }
    setSortConfig({ key, direction });
  };

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
  if (!albarans?.length)
    return (
      <Container py="xl">
        <Button
          onClick={() => navigate("/albarans/new")}
          style={{ marginBottom: 16 }}
        >
          NOU ALBARÀ
        </Button>
        <Text>No hi ha albarans.</Text>
      </Container>
    );


  console.log("🔗 URL base per fitxers (AlbaransList):", API_BASE_URL);

  return (
    <Container style={{ fontFamily: "Poppins, sans-serif" }} py="xl">
      <Flex mb="md" align="center" justify="space-between">
        <Title order={2} style={{ fontFamily: "Poppins, sans-serif" }}>
          ALBARANS
        </Title>
      </Flex>
      <div
        style={{
          display: "flex",
          flexWrap: "wrap",
          gap: theme.spacing.sm, // Espacio entre los campos
          marginBottom: theme.spacing.sm,
          fontFamily: "Poppins, sans-serif",
          marginTop: "10px",
        }}
      >
        <TextInput
          label="Factura ID"
          placeholder="Filtrar ID"
          value={filterId}
          onChange={(e) => setFilterId(e.currentTarget.value)}
          style={{ flex: "1 1 5%" }}
        />
        <TextInput
          label="Referència"
          placeholder="Filtrar referència"
          value={filterRef}
          onChange={(e) => setFilterRef(e.currentTarget.value)}
          style={{ flex: "1 1 5%" }}
        />
        <TextInput
          label="Estat"
          placeholder="Filtrar estat"
          value={filterEstat}
          onChange={(e) => setFilterEstat(e.currentTarget.value)}
          style={{ flex: "1 1 5%" }}
        />
        <TextInput
          label="Proveïdor"
          placeholder="Filtrar proveïdor"
          value={filterProv}
          onChange={(e) => setFilterProv(e.currentTarget.value)}
          style={{ flex: "1 1 5%" }}
        />
        <Flex align="flex-end" gap="16px">
          {" "}
          {/* Espacio entre los campos de fecha */}
          <TextInput
            label="Data Des de"
            type="date"
            value={dateFrom}
            onChange={(e) => setDateFrom(e.currentTarget.value)}
            style={{ flex: "1 1 5%" }}
          />
          <TextInput
            label="Fins a"
            type="date"
            value={dateTo}
            onChange={(e) => setDateTo(e.currentTarget.value)}
            style={{ flex: "1 1 5%" }}
          />
        </Flex>
        <Flex align="flex-end" gap="16px">
          {/* Espacio entre los campos de importe */}
          <TextInput
            label="Import mínim"
            placeholder="Min"
            value={importMin}
            onChange={(e) => setImportMin(e.currentTarget.value)}
            style={{ flex: "1 1 5%", marginLeft: "10px", marginRight: "10px" }} // Margen izquierdo y derecho
          />
          <TextInput
            label="Import màxim"
            placeholder="Max"
            value={importMax}
            onChange={(e) => setImportMax(e.currentTarget.value)}
            style={{ flex: "1 1 5%", marginLeft: "10px", marginRight: "10px" }} // Margen izquierdo y derecho
          />
        </Flex>
      </div>

      <Group gap="sm">
        <Button
          onClick={() => navigate("/albarans/new")}
          style={{ marginTop: "20px", marginRight: "10px" }}
        >
          Nou Albarà
        </Button>
        <Button
          variant="outline"
          onClick={() => {
            setFilterId("");
            setFilterRef("");
            setFilterEstat("");
            setFilterProv("");
            setFilterFactura("");
            setDateFrom("");
            setDateTo("");
            setImportMin("");
            setImportMax("");
            setFilterImage("all");
          }}
        >
          Netejar filtre
        </Button>
      </Group>
      <ScrollArea>
        <Table
          striped
          highlightOnHover
          withColumnBorders
          style={{ borderSpacing: "0 10px", borderCollapse: "separate" }}
        >
          <thead>
            <tr>
              {(
                [
                  "ID",
                  "Referència",
                  "Data",
                  "Import",
                  "Estat",
                  "Proveïdor",
                  "Factura",
                  "Imatge",
                ] as const
              ).map((header, idx) => {
                const key = (
                  [
                    "id",
                    "referenciaDocument",
                    "data",
                    "importTotal",
                    "estat",
                    "proveidor",
                    "factura",
                    "fitxerAdjunt",
                  ] as SortKey[]
                )[idx];
                const isActive = sortConfig?.key === key;
                return (
                  <th
                    key={key}
                    style={{
                      cursor: "pointer",
                      userSelect: "none",
                      padding: 8,
                    }}
                    onClick={() => requestSort(key)}
                  >
                    <Flex align="center" gap="4px">
                      {header}
                      {isActive ? (
                        sortConfig?.direction === "asc" ? (
                          <IconChevronUp size={14} />
                        ) : (
                          <IconChevronDown size={14} />
                        )
                      ) : null}
                    </Flex>
                  </th>
                );
              })}
              <th style={{ padding: 8 }}>Accions</th>
            </tr>
          </thead>
          <tbody>
            {paginated.map((a, index) => (
              <tr
                key={a.id}
                style={{
                  backgroundColor: index % 2 === 0 ? "#f9f9f9" : "#eaeaea",
                  borderRadius: "8px",
                }}
              >
                <td style={{ padding: 10 }}>{a.id}</td>
                <td style={{ padding: 10 }}>{a.referenciaDocument}</td>
                <td style={{ padding: 10 }}>
                  {new Date(a.data).toLocaleDateString()}
                </td>
                <td style={{ padding: 10 }}>{a.importTotal.toFixed(2)} €</td>
                <td
                  style={{
                    padding: 10,
                    color:
                      a.estat === "VALIDAT"
                        ? "#155724" // Verd si és VALIDAT
                        : a.estat === "EN_CURS"
                        ? "#856404" // Taronja si és EN_CURS
                        : undefined, // Color predeterminat per altres valors
                    backgroundColor:
                      a.estat === "VALIDAT"
                        ? "#d4edda" // Verd clar si és VALIDAT
                        : a.estat === "EN_CURS"
                        ? "#fff3cd" // Taronja clar si és EN_CURS
                        : undefined, // Fons predeterminat per altres valors
                  }}
                >
                  {a.estat}
                </td>                <td style={{ padding: 10 }}>{a.proveidor?.nom || "-"}</td>
                <td style={{ padding: 10 }}>
                  {a.factura?.id ? (
                    <a
                      href={`/factures/${a.factura.id}`}
                      target="_blank"
                      rel="noopener noreferrer"
                      style={{ color: "#1a73e8", textDecoration: "underline" }}
                    >
                      #{a.factura.id}
                    </a>
                  ) : (
                    "-"
                  )}
                </td>
                <td style={{ padding: 10 }}>
                  {a.fitxerAdjunt ? (
                    <a
                      href={`${API_BASE_URL}/fitxers/albara/${a.id}`}
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
                <td style={{ padding: 10 }}>
                  <Button
                    variant="outline"
                    size="xs"
                    onClick={() => navigate(`/albarans/${a.id}/edit`)}
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
