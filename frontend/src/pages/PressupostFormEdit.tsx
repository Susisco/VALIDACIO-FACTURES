// src/pages/PressupostFormEdit.tsx
import React, { useEffect, useState } from "react";
import { useForm, SubmitHandler, Controller } from "react-hook-form";
import { useNavigate, useParams } from "react-router-dom";
import {
  PressupostInput,
  usePressupost,
  useUpdatePressupost,
  useDeletePressupost,
} from "../api/pressupostos";
import {
  Paper,
  TextInput,
  Button,
  Loader,
  Text,
  Title,
  Flex,
  Group,
  NumberInput,
  Popover,
  ScrollArea,
  Box,
} from "@mantine/core";
import { showNotification } from "@mantine/notifications";
import { IconCheck, IconX } from "@tabler/icons-react";
import { useProveidors } from "../api/proveidors";

// Tipus de dades per al formulari
type FormValues = {
  referenciaDocument: string;
  fitxerAdjunt?: string;
  importTotal: number;
  proveidorId: number;
};

export default function PressupostFormEdit() {
  const { id } = useParams<{ id: string }>();
  const pressId = Number(id);
  const navigate = useNavigate();

  const { data: item, isLoading, error } = usePressupost(pressId);
  const updateMutation = useUpdatePressupost();
  const deleteMutation = useDeletePressupost();
  const { data: proveidors = [] } = useProveidors();

  const [opened, setOpened] = useState(false);

  const {
    register,
    control,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<FormValues>();

  useEffect(() => {
    if (item && proveidors.length > 0) {
      reset({
        referenciaDocument: item.referenciaDocument,
        fitxerAdjunt: item.fitxerAdjunt ?? undefined,
        importTotal: item.importTotal,
        proveidorId: item.proveidor.id,
      });
    }
  }, [item, proveidors, reset]);

  const onSubmit: SubmitHandler<FormValues> = (data) => {
    if (!item) return;

    const payload: PressupostInput = {
      ...item,
      referenciaDocument: data.referenciaDocument,
      fitxerAdjunt: data.fitxerAdjunt ?? null,
      importTotal: data.importTotal,
      proveidorId: data.proveidorId,
    };

    updateMutation.mutate(
      { id: pressId, data: payload },
      {
        onSuccess: () => {
          showNotification({
            title: "Correcte",
            message: "Desats canvis",
            icon: <IconCheck size={18} />,
            color: "teal",
          });
          navigate("/pressupostos");
        },
        onError: (err: unknown) => {
          const msg = err instanceof Error ? err.message : String(err);
          showNotification({
            title: "Error",
            message: msg,
            icon: <IconX size={18} />,
            color: "red",
          });
        },
      }
    );
  };

  const handleDelete = () => {
    if (!window.confirm("Segur que vols eliminar aquest pressupost?")) return;

    deleteMutation.mutate(pressId, {
      onSuccess: () => {
        showNotification({
          title: "Eliminat",
          message: "Pressupost eliminat correctament",
          color: "teal",
        });
        navigate("/pressupostos");
      },
      onError: (err) => {
        const msg = err instanceof Error ? err.message : "Error en eliminar";
        showNotification({
          title: "Error",
          message: msg,
          color: "red",
        });
      },
    });
  };

  if (isLoading || !item) return <Loader />;
  if (error) return <Text color="red">Error: {error.message}</Text>;

  // Comprovem si item.factura és un objecte i no és null, per amagar botons edit i eliminar
  const hasFactura = typeof item.factura === "object" && item.factura !== null;

  const FILE_URL = `${API_BASE_URL}/fitxers/albara/${albaraId}`;

  
  return (
    <Paper
      p="lg"
      radius="md"
      shadow="sm"
      style={{
        maxWidth: 1000,
        margin: "auto",
        fontFamily: "Poppins, sans-serif",
      }}
    >
      <Title order={3} mb="md">
        Editar Pressupost
      </Title>
      <form onSubmit={handleSubmit(onSubmit)}>
        <Group
          grow
          mb="md"
          style={{
            display: "flex",
            flexWrap: "nowrap",
            gap: "16px",
            fontFamily: "Poppins, sans-serif",
          }}
        >
          <TextInput size="sm" label="ID" value={pressId.toString()} disabled />
          <TextInput size="sm" label="Estat" value={item.estat} disabled />
          <TextInput size="sm" label="Tipus" value={item.tipus} disabled />
          <TextInput
            size="sm"
            label="Factura ID"
            value={
              typeof item.factura === "object" && item.factura !== null
                ? item.factura.id.toString()
                : "No assignada"
            }
            disabled
          />
          {typeof item.factura === "object" && item.factura !== null ? (
            <Text
              size="sm"
              mt="xs"
              component="a"
              href={`/factures/${item.factura.id}`}
              target="_blank"
              rel="noopener noreferrer"
              style={{ color: "#1a73e8", textDecoration: "underline" }}
            >
              Factura #{item.factura.id}
            </Text>
          ) : (
            <TextInput
              size="sm"
              label="Factura ID"
              value="No assignada"
              disabled
            />
          )}
        </Group>

        <Group
          grow
          mb="md"
          style={{
            display: "flex",
            flexWrap: "nowrap",
            gap: "16px",
            fontFamily: "Poppins, sans-serif",
            marginTop: "16px",
          }}
        >
          <TextInput
            size="sm"
            label="Referència"
            {...register("referenciaDocument", {
              required: "La referència és obligatòria",
            })}
          />
          <TextInput
            size="sm"
            label="Data"
            value={item.data.slice(0, 10)}
            disabled
          />

          <NumberInput
            size="sm"
            label="Import Total"
            defaultValue={item.importTotal}
            step={0.01}
            {...register("importTotal", {
              required: "L'import total és obligatori",
            })}
            onChange={(value) => {
              const parsedValue = parseFloat(
                value?.toString().replace(/€\s?|(,*)/g, "") || "0"
              );
              register("importTotal").onChange({
                target: { value: parsedValue },
              });
            }}
            min={0}
            max={1000000}
          />

          <Controller
            name="proveidorId"
            control={control}
            rules={{ required: true }}
            render={({ field }) => {
              const selectedProveidor = proveidors.find(
                (p) => p.id === (field.value ?? item?.proveidor?.id)
              );
              if (!field.value && item?.proveidor.id) {
                field.onChange(item.proveidor.id);
              }
              return (
                <Popover
                  opened={opened}
                  onClose={() => setOpened(false)}
                  position="bottom-start"
                  withArrow
                  width={300}
                >
                  <Popover.Target>
                    <Flex align="flex-end" gap="sm">
                      <TextInput
                        size="sm"
                        label="Proveïdor"
                        placeholder="Clica per triar"
                        value={selectedProveidor?.nomComercial || ""}
                        readOnly
                        error={errors.proveidorId && "Obligatori"}
                        onClick={() => setOpened(true)}
                      />
                      <Button size="sm" onClick={() => setOpened(true)}>
                        Tria
                      </Button>
                    </Flex>
                  </Popover.Target>
                  <Popover.Dropdown>
                    <ScrollArea style={{ height: 200 }}>
                      {proveidors.map((p) => (
                        <Box
                          key={p.id}
                          style={{ padding: 8, cursor: "pointer" }}
                          onClick={() => {
                            field.onChange(p.id);
                            setOpened(false);
                          }}
                        >
                          <Text fw={500}>{p.nomComercial}</Text>
                          <Text size="xs" color="gray">
                            {p.nif}
                          </Text>
                        </Box>
                      ))}
                    </ScrollArea>
                  </Popover.Dropdown>
                </Popover>
              );
            }}
          />
        </Group>

        <Group
          grow
          mb="md"
          style={{
            display: "flex",
            flexWrap: "nowrap",
            gap: "16px",
            fontFamily: "Poppins, sans-serif",
            paddingTop: "26px",
          }}
        >
          {" "}
          <TextInput
            size="sm"
            label="Fitxer Adjunt"
            placeholder="URL fitxer adjunt"
            {...register("fitxerAdjunt")}
            disabled
          />
          {item.fitxerAdjunt ? (
            
            <a
              href={FILE_URL}
              target="_blank"
              rel="noopener noreferrer"
              style={{
                color: "#1a73e8",
                textDecoration: "underline",
                fontSize: "0.9rem",
              }}
            >
              Veure fitxer adjunt
            </a>
          ) : (
            <Text size="sm" color="dimmed">
              No hi ha fitxer adjunt
            </Text>
          )}
          {item.fitxerAdjunt &&
            (item.fitxerAdjunt.endsWith(".jpg") ||
              item.fitxerAdjunt.endsWith(".jpeg") ||
              item.fitxerAdjunt.endsWith(".png")) && (
              <Box mt="sm">
                <Text size="xs" color="dimmed" mb={4}>
                  Vista prèvia:
                </Text>
                <img
                  src={FILE_URL}
                  alt="Vista prèvia"
                  style={{
                    maxWidth: "100%",
                    maxHeight: "200px",
                    borderRadius: "8px",
                    border: "1px solid #ccc",
                  }}
                />
              </Box>
            )}
        </Group>
        <Group
          grow
          mb="md"
          style={{
            display: "flex",
            flexWrap: "nowrap",
            gap: "16px",
            fontFamily: "Poppins, sans-serif",
          }}
        >
          {" "}
          {!hasFactura && (
            <>
              <Button
                size="sm"
                type="submit"
                loading={updateMutation.status === "pending"}
              >
                Desa
              </Button>
              <Button color="red" variant="outline" onClick={handleDelete}>
                Elimina
              </Button>
            </>
          )}
          <Button
            size="sm"
            variant="outline"
            onClick={() => navigate("/pressupostos")}
          >
            Cancel·la
          </Button>
        </Group>
      </form>
    </Paper>
  );
}
