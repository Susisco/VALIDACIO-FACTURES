import React, { useEffect, useState } from "react";
import { useForm, useFieldArray, Controller } from "react-hook-form";
import { useNavigate } from "react-router-dom";
import {
  Paper,
  Title,
  TextInput,
  Button,
  Group,
  Divider,
  ActionIcon,
  Text,
  Flex,
} from "@mantine/core";
import { Trash } from "tabler-icons-react";
import { showNotification } from "@mantine/notifications";
import { useProveidors } from "../api/proveidors";
import {
  useCreateFactura,
  DetallInput,
  FacturaInput,
  FacturaCreateInput,
} from "../api/factures";

import ModalProveidorSelector from "../components/ModalProveidorSelector";
import type { Proveidor } from "../api/proveidors";

export default function FacturaFormNew() {
  const navigate = useNavigate();
  const { data: proveidors = [] } = useProveidors();
  const { mutate: createFactura, status: saving } = useCreateFactura();

  const [userId, setUserId] = useState<number | null>(null);
  const [modalObrir, setModalObrir] = useState(false);

  const {
    register,
    control,
    handleSubmit,
    setValue,
    watch,
    formState: { errors },
  } = useForm<FacturaInput>({
    defaultValues: {
      data: "",
      tipus: "FACTURA",
      referenciaDocument: "",
      estat: "EN_CURS",
      proveidorId: 0,
      detalls: [{ referenciaDocumentDetall: "", importTotalDetall: 0 }],
      importTotal: 0,
    },
  });

  const { fields, append, remove } = useFieldArray({
    control,
    name: "detalls",
  });

  const detalls = watch("detalls");
  const currentTotal = watch("importTotal");

  useEffect(() => {
    const id = localStorage.getItem("usuariId");
    setUserId(id ? parseInt(id, 10) : 0);
  }, []);

  const [importRecalculat, setImportRecalculat] = useState(0);

  useEffect(() => {
    const total = detalls.reduce(
      (sum, d) => sum + parseFloat(d.importTotalDetall?.toString() || "0"),
      0
    );
    const rounded = parseFloat(total.toFixed(2));
    setImportRecalculat(rounded);
  }, [detalls]);

  const totalsMatch =
    detalls.length > 0 &&
    currentTotal > 0 &&
    Math.abs(currentTotal - importRecalculat) < 0.01;

  const onSubmit = (data: FacturaInput) => {
    if (data.detalls.length === 0) {
      showNotification({
        title: "Error",
        message: "Cal afegir almenys una línia",
        color: "red",
      });
      return;
    }

    const detallsValids = data.detalls.filter(
      (d) =>
        d.referenciaDocumentDetall.trim() !== "" &&
        Number(d.importTotalDetall) > 0
    );

    if (detallsValids.length !== data.detalls.length) {
      showNotification({
        title: "Error",
        message: "Totes les línies han de tenir referència i import > 0",
        color: "red",
      });
      return;
    }

    const detallsNormalitzats: DetallInput[] = detallsValids.map((d) => ({
      ...d,
      importTotalDetall: parseFloat(d.importTotalDetall.toString()),
    }));

    const payload: FacturaCreateInput = {
      ...data,
      proveidor: { id: data.proveidorId },
      detalls: detallsNormalitzats,
    };

    createFactura(payload, {
      onSuccess: () => {
        showNotification({
          title: "Correcte",
          message: "Factura creada",
          color: "teal",
        });
        navigate("/factures/Detall");
      },
      onError: (error) => {
        showNotification({
          title: "Error",
          message: String(error),
          color: "red",
        });
      },
    });
  };

  const recalcularTotal = () => {
    const total = detalls.reduce(
      (sum, d) => sum + parseFloat(d.importTotalDetall?.toString() || "0"),
      0
    );
    const rounded = parseFloat(total.toFixed(2));
    setImportRecalculat(rounded);
  };

  const [selectedProveidor, setSelectedProveidor] = useState<Proveidor | null>(
    null
  );

  if (userId === null) return <Text>Carregant usuari...</Text>;
  if (userId === 0)
    return (
      <Text color="red">Error: No s'ha pogut carregar l'usuari actiu.</Text>
    );

  return (
    <Paper
      p="lg"
      radius="md"
      shadow="sm"
      style={{ maxWidth: 900, margin: "auto" }}
    >
      <ModalProveidorSelector
        opened={modalObrir}
        onClose={() => setModalObrir(false)}
        proveidors={proveidors}
        onSelect={(p) => {
          setSelectedProveidor(p);
          setValue("proveidorId", p.id);
        }}
      />
      <Title order={3} mb="md">
        Nova Factura
      </Title>

      <form onSubmit={handleSubmit(onSubmit)}>
        <Group grow mb="md">
          <TextInput label="Tipus" disabled {...register("tipus")} />
          <TextInput
            label="Referència"
            {...register("referenciaDocument", { required: true })}
            error={errors.referenciaDocument && "Obligatori"}
          />
          <TextInput
            label="Data"
            type="date"
            {...register("data", { required: true })}
            error={errors.data && "Obligatori"}
          />
        </Group>

        <Flex align="flex-end" gap="sm" mb="md">
          <TextInput
            label="Proveïdor"
            placeholder="Clica per triar"
            value={selectedProveidor?.nomComercial || ""}
            readOnly
            error={errors.proveidorId && "Obligatori"}
            onClick={() => setModalObrir(true)}
          />

          <Button onClick={() => setModalObrir(true)}>Tria</Button>
        </Flex>

        <Group grow mb="md">
          <Controller
            control={control}
            name="importTotal"
            rules={{
              required: "Obligatori",
              validate: (v) =>
                parseFloat(v?.toString() || "0") > 0 ||
                "Ha de ser més gran que 0",
            }}
            render={({ field }) => (
              <TextInput
                label="Import Total (manual)"
                {...field}
                onChange={(e) => {
                  const value = e.target.value;
                  if (/^\d*\.?\d*$/.test(value)) field.onChange(value);
                }}
                onBlur={() =>
                  field.onChange(parseFloat(field.value.toString()).toFixed(2))
                }
                value={field.value || ""}
              />
            )}
          />

          <TextInput
            label="Import recalculat"
            disabled
            value={`${importRecalculat.toFixed(2)} €`}
            styles={{
              input: { color: totalsMatch ? "green" : "red", fontWeight: 600 },
            }}
          />

          {currentTotal !== importRecalculat && (
            <Text
              size="sm"
              color={totalsMatch ? "green" : "red"}
              mt={-10}
              ml={4}
              style={{ fontStyle: "italic" }}
            >
              {importRecalculat < currentTotal
                ? `Falten ${(currentTotal - importRecalculat).toFixed(2)} €`
                : `S'obren ${(importRecalculat - currentTotal).toFixed(2)} €`}
            </Text>
          )}
        </Group>

        <Divider label="Línies de factura" labelPosition="center" my="md" />
        <Button
          variant="light"
          onClick={() =>
            append({ referenciaDocumentDetall: "", importTotalDetall: 0 })
          }
        >
          Afegir línia
        </Button>

        <table
          style={{
            width: "100%",
            fontFamily: "Poppins, sans-serif",
            borderCollapse: "collapse",
          }}
        >
          <thead>
            <tr>
              <th style={{ padding: 8 }}>Referència línia</th>
              <th style={{ padding: 8 }}>Import línia</th>
              <th style={{ padding: 8 }}>Accions</th>
            </tr>
          </thead>
          <tbody>
            {fields.map((field, index) => (
              <tr key={field.id}>
                <td style={{ padding: 8 }}>
                  <TextInput
                    variant="unstyled"
                    placeholder="Referència línia"
                    {...register(
                      `detalls.${index}.referenciaDocumentDetall` as const,
                      { required: true }
                    )}
                  />
                </td>
                <td style={{ padding: 8 }}>
                  <Controller
                    control={control}
                    name={`detalls.${index}.importTotalDetall` as const}
                    render={({ field }) => (
                      <TextInput
                        variant="unstyled"
                        placeholder="Import línia"
                        {...field}
                        onChange={(e) => {
                          const value = e.target.value;
                          if (/^\d*\.?\d*$/.test(value)) field.onChange(value);
                        }}
                        onBlur={() =>
                          field.onChange(
                            parseFloat(field.value.toString()).toFixed(2)
                          )
                        }
                        value={field.value || ""}
                      />
                    )}
                  />
                </td>
                <td style={{ padding: 8 }}>
                  <Group>
                    <ActionIcon color="blue" onClick={recalcularTotal}>
                      🔄
                    </ActionIcon>
                    <ActionIcon
                      color="red"
                      onClick={() => remove(index)}
                      style={{ marginLeft: 12 }}
                    >
                      <Trash size={16} />
                    </ActionIcon>
                  </Group>
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        <Group justify="space-between" mt="xl">
          <Button
            type="submit"
            loading={saving === "pending"}
            disabled={!totalsMatch}
          >
            Desa canvis
          </Button>
          <Button
            variant="outline"
            onClick={() => navigate("/factures/Detall")}
          >
            Cancel·la
          </Button>
        </Group>
      </form>
    </Paper>
  );
}
