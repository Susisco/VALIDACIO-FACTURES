// src/pages/FacturesListDetall.tsx
import React, { useState, useMemo } from "react";
import { useFactures } from "../api/factures";//../api/factures es la ruta a la carpeta no la ruta http 
import {
  Container,
  Group,
  Button,
  Loader,
  Text,
  Table,
  Collapse,
  ScrollArea,
  Pagination,
  TextInput,
  useMantineTheme,
} from "@mantine/core";
import { useNavigate } from "react-router-dom";

// Sort configuration type
type SortConfig = {
  key:
    | "id"
    | "data"
    | "referenciaDocument"
    | "estat"
    | "tipus"
    | "importTotal"
    | "proveidor";
  direction: "asc" | "desc";
};

export default function FacturesListDetall() {
  const navigate = useNavigate();
  const theme = useMantineTheme();
  const { data: factures, isLoading, error } = useFactures();

  // Estados de expansión, selección, orden y filtros
  const [expandedIds, setExpandedIds] = useState<Set<number>>(new Set());
  const [selectedId, setSelectedId] = useState<number | null>(null);
const [sortConfig, setSortConfig] = useState<SortConfig | null>({
  key: "id",
  direction: "desc",
});  const [idFilter, setIdFilter] = useState("");
  const [dateFilter, setDateFilter] = useState("");
  const [impMin, setImpMin] = useState<number | undefined>(undefined);
  const [impMax, setImpMax] = useState<number | undefined>(undefined);
  const [refFilter, setRefFilter] = useState("");
  const [provFilter, setProvFilter] = useState("");
  const [estatFilter, setEstatFilter] = useState("");
  const [page, setPage] = useState(1);

  const pageSize = 20;

  // Alterna expansión de fila
  const toggle = (id: number) => {
    const next = new Set(expandedIds);
    // eslint-disable-next-line @typescript-eslint/no-unused-expressions
    next.has(id) ? next.delete(id) : next.add(id);
    setExpandedIds(next);
  };

  // Manejo de sort
  const handleSort = (key: SortConfig["key"]) => {
    const direction =
      sortConfig?.key === key && sortConfig.direction === "asc"
        ? "desc"
        : "asc";
    setSortConfig({ key, direction });
    setPage(1);
  };
  const getArrow = (key: SortConfig["key"]) => {
    if (!sortConfig || sortConfig.key !== key) return "⇅";
    return sortConfig.direction === "asc" ? "↑" : "↓";
  };

  // Ordena según sortConfig
  const sortedFactures = useMemo(() => {
    if (!factures) return [];
    const list = [...factures];
    if (sortConfig) {
      list.sort((a, b) => {
        let aValue: unknown, bValue: unknown;
        if (sortConfig.key === "data") {
          aValue = new Date(a.data).getTime();
          bValue = new Date(b.data).getTime();
        } else if (sortConfig.key === "proveidor") {
          aValue = a.proveidor.nomComercial.toLowerCase();
          bValue = b.proveidor.nomComercial.toLowerCase();
        } else {
          aValue = (a as unknown as Record<string, unknown>)[sortConfig.key];
          bValue = (b as unknown as Record<string, unknown>)[sortConfig.key];
        }
        if (
          (aValue as unknown as unknown as unknown as unknown as unknown as unknown as unknown as
            | string
            | number) < (bValue as string | number)
        )
          return sortConfig.direction === "asc" ? -1 : 1;
        if (
          (aValue as unknown as unknown as unknown as unknown as
            | string
            | number) > (bValue as string | number)
        )
          return sortConfig.direction === "asc" ? 1 : -1;
        return 0;
      });
    }
    return list;
  }, [factures, sortConfig]);

  // Aplica filtros
  const filteredFactures = useMemo(() => {
    return sortedFactures.filter((f) => {
      if (idFilter && f.id !== +idFilter) return false;
      if (dateFilter && !f.data.startsWith(dateFilter)) return false;
      if (impMin !== undefined && f.importTotal < impMin) return false;
      if (impMax !== undefined && f.importTotal > impMax) return false;
      if (
        refFilter &&
        !f.referenciaDocument.toLowerCase().includes(refFilter.toLowerCase())
      )
        return false;
      if (
        provFilter &&
        !f.proveidor.nomComercial
          .toLowerCase()
          .includes(provFilter.toLowerCase())
      )
        return false;
      if (
        estatFilter &&
        !f.estat.toLowerCase().includes(estatFilter.toLowerCase())
      )
        return false;
      return true;
    });
  }, [
    sortedFactures,
    idFilter,
    dateFilter,
    impMin,
    impMax,
    refFilter,
    provFilter,
    estatFilter,
  ]);

  const totalPages = Math.ceil(filteredFactures.length / pageSize);
  const paginated = filteredFactures.slice(
    (page - 1) * pageSize,
    page * pageSize
  );

  // Estados especiales
  if (isLoading) {
    return (
      <Container
        py="xl"
        style={{ textAlign: "center", fontFamily: "Poppins, sans-serif" }}
      >
        <Loader />
      </Container>
    );
  }
  if (error) {
    return (
      <Container py="xl" style={{ fontFamily: "Poppins, sans-serif" }}>
        <Text color="red" fw={500} fz="lg">
          Error: {error.message}
        </Text>
      </Container>
    );
  }
  if (!factures?.length) {
    return (
      <Container py="xl" style={{ fontFamily: "Poppins, sans-serif" }}>
                 <Button onClick={() => navigate('/factures/new')}>
                   Nova Factura
                 </Button>
        <Text >
          No hi ha factures.
        </Text>
      </Container>
    );
  }

  // Render principal
  /****
    *  Aquí es donde se renderiza la tabla de factures, con los filtros y la paginación.
   */
  return (
    <Container>
      {/* Filters + Actions in one row 
      **  Aquí es donde se renderiza la tabla de factures, con los filtros y la paginación.
      **  Se utiliza el componente Table de Mantine para mostrar los datos de forma tabular.
      **  Los filtros se aplican a los datos antes de paginarlos, y la paginación se maneja con el estado de la página.
      **  La tabla también incluye una funcionalidad de ordenación y expansión de filas para mostrar detalles adicionales de cada factura.
      **  Los botones de acción permiten editar cada factura individualmente.
      
      */}
      <div
        style={{
          display: "flex",
          flexWrap: "wrap",
          gap: theme.spacing.sm,
          marginBottom: theme.spacing.sm,
          fontFamily: "Poppins, sans-serif",
          marginTop: "10px",
        }}
      >
        <TextInput
          label="ID"
          placeholder="Filtrar ID"
          value={idFilter}
          onChange={(e) => setIdFilter(e.currentTarget.value)}
          style={{ flex: "1 1 5%" }}
        />
        <TextInput
          label="Data"
          type="date"
          value={dateFilter}
          onChange={(e) => setDateFilter(e.currentTarget.value)}
          style={{ flex: "1 1 1%" }}
        />
        <TextInput
          label="Import mínim"
          placeholder="Min"
          value={impMin !== undefined ? impMin.toString() : ""}
          onChange={(e) =>
        setImpMin(
          e.currentTarget.value
            ? parseFloat(e.currentTarget.value)
            : undefined
        )
          }
          style={{ flex: "1 1 5%" }}
        />
        <TextInput
          label="Import màxim"
          placeholder="Max"
          value={impMax !== undefined ? impMax.toString() : ""}
          onChange={(e) =>
        setImpMax(
          e.currentTarget.value
            ? parseFloat(e.currentTarget.value)
            : undefined
        )
          }
          style={{ flex: "1 1 5%" }}
        />
        <TextInput
          label="Ref. Factura"
          placeholder="Filtrar ref."
          value={refFilter}
          onChange={(e) => setRefFilter(e.currentTarget.value)}
          style={{ flex: "1 1 5%" }}
        />
        <TextInput
          label="Proveïdor"
          placeholder="Filtrar proveïdor"
          value={provFilter}
          onChange={(e) => setProvFilter(e.currentTarget.value)}
          style={{ flex: "1 1 5%" }}
        />
        <TextInput
          label="Estat"
          placeholder="Filtrar estat"
          value={estatFilter}
          onChange={(e) => setEstatFilter(e.currentTarget.value)}
          style={{ flex: "1 1 5%" }}
        />
      </div>
      <Button
        onClick={() => navigate("/factures/new")}
        style={{ marginTop: "20px", marginRight: "10px" }}
      >
        Nova Factura
      </Button>
      <Button
        variant="outline"
        style={{ marginTop: "20px" ,}}
        onClick={() => {
          setIdFilter("");
          setDateFilter("");
          setImpMin(undefined);
          setImpMax(undefined);
          setRefFilter("");
          setProvFilter("");
          setEstatFilter("");
          setPage(1);
        }}
      >
        Netejar
      </Button>

      {/*
        *  Aquí es donde se renderiza la tabla de factures, con los filtros y la paginación.
        *  Se utiliza el componente Table de Mantine para mostrar los datos de forma tabular.
        *  Los filtros se aplican a los datos antes de paginarlos, y la paginación se maneja con el estado de la página.
        *  La tabla también incluye una funcionalidad de ordenación y expansión de filas para mostrar detalles adicionales de cada factura.
        *  Los botones de acción permiten editar cada factura individualmente.  
        * 
       Tabla sortida de dades*/}

      <ScrollArea
        style={{ fontFamily: "Poppins, sans-serif", marginTop: "20px" }}
      >
        <Table
          striped
          highlightOnHover
          verticalSpacing="sm"
          horizontalSpacing="md"
          style={{ fontFamily: "Poppins, sans-serif" }}
          
        >
          {/* Aquí se renderizan los encabezados de la tabla, incluyendo los filtros y las acciones. */}
          <thead style={{ fontFamily: "Poppins, sans-serif" }}>
            <tr>
              <th />
              {(
                [
                  "id",
                  "importTotal",
                  "estat",
                  "data",
                  "referenciaDocument",
                  "proveidor",
                ] as SortConfig["key"][]
              ).map((key) => (
                <th
                  key={key}
                  style={{ cursor: "pointer" }}
                  onClick={() => handleSort(key)}
                >
                  {key === "referenciaDocument"
                    ? "Ref. Factura"
                    : key === "importTotal"
                    ? "Import"
                    : key === "estat"
                    ? "Estat"
                    : key.charAt(0).toUpperCase() + key.slice(1)}{" "}
                  {getArrow(key)}
                </th>
              ))}
              <th>Accions</th>
            </tr>
          </thead>

          {/* Aquí se renderizan las filas de la tabla, incluyendo los detalles de cada factura. */}
          <tbody>
            {paginated.map((f, i) => (
              <React.Fragment key={f.id}>
                <tr
                  onClick={() => setSelectedId(f.id)}
                  style={{
                    backgroundColor:
                      selectedId === f.id
                        ? theme.colors.gray[3]
                        : i % 2 === 0
                        ? theme.colors.gray[0]
                        : theme.white,
                    cursor: "pointer",
                  }}
                >
                  <td>
                    <Button variant="subtle" onClick={() => toggle(f.id)}>
                      {expandedIds.has(f.id) ? "▴" : "▾"}
                    </Button>
                  </td>
                  <td
                    style={{ borderRight: "3px solid #ccc", padding: "10px" }}
                  >
                    {f.id}
                  </td>
                    <td
                    style={{
                      borderRight: "3px solid #ccc",
                      padding: "10px",
                      color:
                      f.detalls.reduce(
                        (sum, d) => sum + d.importTotalDetall,
                        0
                      ) !== f.importTotal
                        ? "red"
                        : "inherit",
                    }}
                    >
                    {f.importTotal.toFixed(2)}
                    </td>
<td
  style={{
    borderRight: "3px solid #ccc",
    padding: "10px",
    color: f.estat === "VALIDAT" ? theme.colors.green[8] : undefined,
    backgroundColor: f.estat === "VALIDAT" ? theme.colors.green[1] : undefined,
  }}
>
  {f.estat}
</td>

                  <td
                    style={{ borderRight: "3px solid #ccc", padding: "10px" }}
                  >
                    {new Date(f.data).toLocaleDateString()}
                  </td>
                  <td
                    style={{ borderRight: "3px solid #ccc", padding: "10px" }}
                  >
                    {f.referenciaDocument}
                  </td>
                  <td
                    style={{ borderRight: "3px solid #ccc", padding: "10px" }}
                  >
                    {f.proveidor.nomComercial}
                  </td>
                  <td>
                    <Button
                      variant="outline"
                      onClick={() => navigate(`/factures/${f.id}`)} //no va al backend, el que fa es carregar el component facturaEdit i aqui es on es crida el backend per mostrar les dades
                    >
                      Editar
                    </Button>
                  </td>
                </tr>
                <tr>
                  <td colSpan={8} style={{ padding: 0, border: 0 }}>
                    <Collapse in={expandedIds.has(f.id)}>
                      <div
                        style={{
                          padding: theme.spacing.md,
                          backgroundColor: theme.colors.gray[1],
                          borderRadius: 8,
                        }}
                      >
                        <Text fw={500}>Línies:</Text>
                        {f.detalls.map((d) => (
                          <Text key={d.id}>
                            {d.referenciaDocumentDetall} –{" "}
                            {d.importTotalDetall.toFixed(2)} €
                          </Text>
                        ))}
                        <Text fw={500}>Albarans:</Text>
                        {f.albaransRelacionats?.length ? (
                          f.albaransRelacionats.map((a) => (
                            <Text key={a.id}>{a.referenciaDocument}</Text>
                          ))
                        ) : (
                          <Text>-</Text>
                        )}
                        <Text fw={500}>Pressupostos:</Text>
                        {f.pressupostosRelacionats?.length ? (
                          f.pressupostosRelacionats.map((p) => (
                            <Text key={p.id}>{p.referenciaDocument}</Text>
                          ))
                        ) : (
                          <Text>-</Text>
                        )}
                      </div>
                    </Collapse>
                  </td>
                </tr>
              </React.Fragment>
            ))}
          </tbody>
        </Table>
      </ScrollArea>

      {/* Paginación */}
      <Group justify="center" mt="md">
        <Pagination value={page} onChange={setPage} total={totalPages} />
      </Group>
    </Container>
  );
}
