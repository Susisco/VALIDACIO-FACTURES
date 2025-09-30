// src/pages/FacturaFormEdit.tsx
import React, { useEffect, useState } from "react";
import { useForm, useFieldArray, Controller } from "react-hook-form";
import { useNavigate, useParams } from "react-router-dom";
import {
  FacturaInput,
  DetallInput,
  useUpdateFactura,
  useUpdateNomesFactura,
  useFactura,
} from "../api/factures";
import {
  Paper,
  Group,
  TextInput,
  Button,
  Title,
  Text,
  Loader,
  Divider,
  ActionIcon,
} from "@mantine/core";
import { Trash } from "tabler-icons-react";
import FacturaMatchAlbarans from "../components/FacturaMatchAlbarans";

interface DetallDto extends DetallInput {
  albaraRelacionatId?: number;
  referenciaAlbaraRelacionat?: string;
  importAlbaraRelacionat?: number;
}

type FormValues = FacturaInput & {
  proveidorId: number;
  detalls: DetallDto[];
};

export default function FacturaFormEdit() {
  const { id } = useParams<{ id: string }>();
  const facturaId = id ? parseInt(id, 10) : undefined;
  const navigate = useNavigate();

  const { data: factura, isLoading, error } = useFactura(facturaId!);//crida al backend per obtenir la factura mitjançant API factures
  const { mutate: updateFactura } = useUpdateFactura();

  // 1) Llamamos al hook de validación/actualización sólo una vez, fuera de cualquier callback
  const { mutate: updateNomesFactura } = useUpdateNomesFactura();

  const { register, control, handleSubmit, reset, watch, setValue, getValues } =
    useForm<FormValues>({
      defaultValues: {
        data: "",
        tipus: "FACTURA",
        referenciaDocument: "",
        estat: "EN_CURS",
        importTotal: 0,
        proveidorId: 0,
        detalls: [{ referenciaDocumentDetall: "", importTotalDetall: 0 }],
      },
    });

  const { fields, remove } = useFieldArray({
    control,
    name: "detalls",
  });

  const [originalTotal, setOriginalTotal] = useState(0);

  useEffect(() => {
    if (factura) {
      reset({
        data: factura.data.slice(0, 10),
        tipus: factura.tipus,
        referenciaDocument: factura.referenciaDocument,
        estat: factura.estat,
        importTotal: factura.importTotal,
        proveidorId: factura.proveidor.id,
        detalls: factura.detalls.map((d) => ({
          referenciaDocumentDetall: d.referenciaDocumentDetall,
          importTotalDetall: d.importTotalDetall,
          albaraRelacionatId: d.albaraRelacionatId,
          referenciaAlbaraRelacionat: d.referenciaAlbaraRelacionat,
          importAlbaraRelacionat: d.importAlbaraRelacionat,
        })),
      });
      setOriginalTotal(factura.importTotal);
    }
  }, [factura, reset]);

  const detalls = watch("detalls");

  const totalDetall = detalls.reduce(
    (sum, d) => sum + parseFloat(d.importTotalDetall?.toString() || "0"),
    0
  );
  const totalAlbara = detalls.reduce(
    (sum, d) => sum + parseFloat(d.importAlbaraRelacionat?.toString() || "0"),
    0
  );

  const importesQuadrats = Math.abs(totalDetall - totalAlbara) < 0.01;

  useEffect(() => {
    setValue("importTotal", totalDetall);
  }, [totalDetall, setValue]);

  const onSubmit = () => {
    if (!facturaId) return;
    const data = getValues();
    const detallsNormalitzats = data.detalls.map((d) => ({
      ...d,
      importTotalDetall: parseFloat(d.importTotalDetall.toString()),
    }));
    updateFactura(
      { id: facturaId, data: { ...data, detalls: detallsNormalitzats } },
      {
        onSuccess: () => navigate("/factures/Detall"),
        onError: (error: Error) =>
          alert("Error al desar la factura: " + error.message),
      }
    );
  };

  if (!facturaId || isLoading) return <Loader />;
  if (error) return <Text color="red">Error: {error.message}</Text>;

  return (
    <Paper
      p="lg"
      radius="md"
      shadow="sm"
      style={{ maxWidth: "100%", margin: "auto" }}
    >
      <Title order={3} mb="md">
        Editar Factura
      </Title>
      <form onSubmit={handleSubmit(onSubmit)}>
        <Group grow mb="md" style={{ display: "flex", gap: "16px" }}>
          <TextInput label="ID" disabled value={id} />
          <TextInput label="Data" type="date" disabled {...register("data")} />
          <TextInput
            label="Tipus"
            disabled
            value={factura?.tipus || "FACTURA"}
          />
          <TextInput
            label="Referència"
            disabled
            {...register("referenciaDocument")}
          />
          <TextInput
            label="Proveïdor"
            disabled
            value={factura?.proveidor.nomComercial}
          />
          <TextInput
            label="Estat"
            disabled
            value={factura?.estat}
            styles={(theme) => ({
              input: {
                // Si el estado es VALIDAT, fondo verde claro y texto verde oscuro
                backgroundColor:
                  factura?.estat === "VALIDAT"
                    ? theme.colors.green[1]
                    : undefined,
                color:
                  factura?.estat === "VALIDAT"
                    ? theme.colors.green[7]
                    : undefined,
              },
              // Opcional: también puedes colorear la etiqueta si quieres
              label: {
                color:
                  factura?.estat === "VALIDAT"
                    ? theme.colors.green[7]
                    : undefined,
              },
            })}
          />
        </Group>

        <div
          style={{
            display: "flex",
            gap: "12px",
            marginBottom: "1rem",
            justifyContent: "flex-start",
            alignItems: "flex-end",
          }}
        >
          <TextInput
            label="Import original"
            disabled
            value={`${originalTotal.toFixed(2)} €`}
            style={{ width: "180px" }}
          />
        </div>

        <Divider label="Línies de factura" labelPosition="center" my="md" />

<table
  style={{ width: "100%", borderCollapse: "collapse", marginTop: 1 }}
>
  <thead>
    <tr>
      <th
        style={{
          borderBottom: "1px solid #ddd",
          padding: "8px",
          textAlign: "left",
        }}
      >
        Referència línia
      </th>
      <th
        style={{
          borderBottom: "1px solid #ddd",
          padding: "8px",
          textAlign: "left",
        }}
      >
        Import línia (Total:{" "}
        {detalls
          .reduce((sum, d) => sum + (d.importTotalDetall || 0), 0)
          .toFixed(2)}{" "}
        €)
      </th>
      <th
        style={{
          borderBottom: "1px solid #ddd",
          padding: "8px",
          textAlign: "left",
        }}
      >
        Albarà vinculat (
        <span
          style={{
            color:
              detalls.reduce(
                (sum, d) => sum + (d.importAlbaraRelacionat || 0),
                0
              ) ===
              detalls.reduce(
                (sum, d) => sum + (d.importTotalDetall || 0),
                0
              )
                ? "green"
                : "red",
          }}
        >
          Total:{" "}
          {detalls
            .reduce(
              (sum, d) => sum + (d.importAlbaraRelacionat || 0),
              0
            )
            .toFixed(2)}{" "}
          €
        </span>
        )
      </th>
      {/* Ya no hay <th> “Accions” */}
    </tr>
  </thead>

  <tbody>
    {fields.map((field, index) => (
      <tr key={field.id}>
        <td style={{ borderBottom: "1px solid #ddd", padding: "8px" }}>
          {detalls[index].referenciaDocumentDetall || "—"}
        </td>
        <td style={{ borderBottom: "1px solid #ddd", padding: "8px" }}>
          {detalls[index].importTotalDetall
            ? parseFloat(detalls[index].importTotalDetall.toString()).toFixed(2) + " €"
            : "0.00 €"}
        </td>
        <td
          style={{
            borderBottom: "1px solid #ddd",
            padding: "8px",
            textAlign: "left",
            color: "gray",
            fontStyle: "italic",
          }}
        >
          {detalls[index].albaraRelacionatId ? (
            <a
              href={`/albarans/${detalls[index].albaraRelacionatId}/edit`}
              target="_blank"
              rel="noopener noreferrer"
              style={{
                color: "#1a73e8",
                textDecoration: "underline",
                display: "block",
              }}
            >
              #{detalls[index].albaraRelacionatId} –{" "}
              {detalls[index].referenciaAlbaraRelacionat || "?"} (
              {detalls[index].importAlbaraRelacionat?.toFixed(2) || "?"} €
            </a>
          ) : (
            "—"
          )}
        </td>
      </tr>
    ))}
  </tbody>
</table>


        <Group justify="space-between" mt="xl" style={{ gap: "16px" }}>
          <Button
            variant="outline"
            onClick={() => navigate("/factures/Detall")}
          >
            Cancel·la
          </Button>

          {/* Botón “Validar” corregido */}
          <Button
            color={
              importesQuadrats && factura?.estat !== "VALIDAT"
                ? "green"
                : "gray"
            }
            disabled={!importesQuadrats || factura?.estat === "VALIDAT"}
            onClick={() => {
              if (!facturaId) return;
              const data = getValues();
              const detallsNormalitzats = data.detalls.map((d) => ({
                ...d,
                importTotalDetall: parseFloat(d.importTotalDetall.toString()),
              }));
              // Ahora llamamos a la función mutate que obtuvimos con el hook
              updateNomesFactura(
                {
                  id: facturaId,
                  data: {
                    ...data,
                    estat: "VALIDAT",
                    detalls: detallsNormalitzats,
                  },
                },
                {
                  onSuccess: () => navigate("/factures/Detall"),
                  onError: (error: Error) =>
                    alert("Error en validar la factura: " + error.message),
                }
              );
            }}
            style={{ marginLeft: "16px" }}
          >
            Validar
          </Button>
        </Group>
      </form>

      {factura?.id && <FacturaMatchAlbarans facturaId={factura.id} />}
    </Paper>
  );
}
