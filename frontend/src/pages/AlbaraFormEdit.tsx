import React, { useEffect, useState } from "react";
import { useForm, SubmitHandler, Controller } from "react-hook-form";
import { useNavigate, useParams } from "react-router-dom";
import {
  Paper,
  TextInput,
  Button,
  Loader,
  Text,
  Title,
  Group,
  NumberInput,
  Flex,
} from "@mantine/core";
import { showNotification } from "@mantine/notifications";
import { IconCheck, IconX } from "@tabler/icons-react";

import {
  useAlbara,
  useUpdateAlbara,
  useDeleteAlbara,
  AlbaraInput,
} from "../api/albarans";
import { useProveidors } from "../api/proveidors";
import type { Proveidor } from "../api/proveidors";

import ModalProveidorSelector from "../components/ModalProveidorSelector";
import { useDisclosure } from "@mantine/hooks";
import HistoricCanvisModal from "../components/HistoricCanvisModal";
import { useHistoricCanvis } from "../api/historic";
import { API_BASE_URL } from "../config/constants";

// Camps editables del formulari
interface FormValues {
  referenciaDocument: string;
  fitxerAdjunt?: string;
  importTotal: number;
  proveidorId: number;
}

function formatDateUTCToLocal(isoString: string): string {
  if (!isoString) return "";
  const date = new Date(isoString);
  return date.toLocaleString("ca-ES", { timeZone: Intl.DateTimeFormat().resolvedOptions().timeZone });
}

export default function AlbaraFormEdit() {
  const { id } = useParams<{ id: string }>();
  const albaraId = Number(id);
  const navigate = useNavigate();

  /* --- Queries & Mutations --- */
  //const { data: item, isLoading, error } = useAlbara(albaraId);
  const { data: item, isLoading, error } = useAlbara(albaraId);
  const updateMutation = useUpdateAlbara();
  const deleteMutation = useDeleteAlbara();

  const { data: proveidors = [] } = useProveidors();

  /* --- Modal de canvis --- */
  const [modalObert, { open, close }] = useDisclosure(false);
  const { data: canvis = [] } = useHistoricCanvis("Albara", albaraId); // albaraId ha de ser el paramètre o id obtingut

  /* --- Modal de proveïdors --- */
  const [modalObrir, setModalObrir] = useState(false);
  const [selectedProveidor, setSelectedProveidor] = useState<Proveidor | null>(
    null
  );
  
  console.log("🔗 URL base per fitxers:", API_BASE_URL);

  /* --- React‑Hook‑Form --- */
  const {
    register,
    control,
    handleSubmit,
    reset,
    setValue,
    formState: { errors },
  } = useForm<FormValues>();

  /* --- Quan rebem l'albarà de l'API --- */
  useEffect(() => {
    if (item) {
      // trobem el proveïdor a la llista per mostrar el seu nom
      const prov = proveidors.find((p) => p.id === item.proveidor.id) || null;
      setSelectedProveidor(prov);

      reset({
        referenciaDocument: item.referenciaDocument,
        fitxerAdjunt: item.fitxerAdjunt ?? undefined,
        importTotal: item.importTotal,
        proveidorId: item.proveidor.id,
      });
    }
  }, [item, proveidors, reset]);

  /*******************/
  /* --- SUBMIT --- */
  const onSubmit: SubmitHandler<FormValues> = (data) => {
    if (!item) return;

    // Comprovem si hi ha canvis
    const hasChanges =
      data.referenciaDocument !== item.referenciaDocument ||
      data.importTotal !== item.importTotal ||
      data.proveidorId !== item.proveidor.id;
    // Si no hi ha canvis, no fem res
    if (!hasChanges) {
      showNotification({
        title: "Sense canvis",
        message: "No s'ha detectat cap canvi per desar.",
        color: "blue",
      });
      return;
    }

    const payload: AlbaraInput = {
      ...item, // conserva tots els camps restants de l'albarà (inclòs facturaId)
      referenciaDocument: data.referenciaDocument,
      fitxerAdjunt: data.fitxerAdjunt ?? null,
      importTotal: data.importTotal,
      proveidorId: data.proveidorId,
      // assegurem el camp facturaId perquè AlbaraInput el requereix
      facturaId:
        (item as unknown as { facturaId?: number }).facturaId ??
        (typeof item.factura === "object" && item.factura !== null
          ? item.factura.id
          : 0),
    };

    updateMutation.mutate(
      { id: albaraId, data: payload }, // enviem l'ID i les dades actualitzades
      {
        onSuccess: () => {
          showNotification({
            title: "Correcte",
            message: "Canvis desats correctament.",
            icon: <IconCheck size={18} />,
            color: "teal",
          });
          navigate("/albarans");
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

  /* --- Eliminar albarà --- */
  const handleDelete = () => {
    if (!item) return;
    if (!window.confirm("Segur que vols eliminar aquest albarà?")) return;

    deleteMutation.mutate(item.id, {
      onSuccess: () => {
        showNotification({
          title: "Eliminat",
          message: "Albarà eliminat correctament",
          color: "teal",
        });
        navigate("/albarans");
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

  /* --- Estat de càrrega / error --- */
  if (isLoading || !item) return <Loader />;
  if (error) return <Text color="red">Error: {error.message}</Text>;

  const hasFactura = typeof item.factura === "object" && item.factura !== null;

  const nomCreador = item.creador?.nom || "Desconegut";
  const dataCreacio = formatDateUTCToLocal(item.creat);
  const dataModificacio = formatDateUTCToLocal(item.actualitzat);
  const nomModificador = item.usuariModificacio?.nom || "Desconegut";

  return (
    <Paper
      p="lg"
      radius="md"
      shadow="sm"
      style={{
        maxWidth: "100%",
        margin: "auto",
        fontFamily: "Poppins, sans-serif",
      }}
    >
      {/* ----- Modal selector de proveïdor ----- */}
      <ModalProveidorSelector
        opened={modalObrir}
        onClose={() => setModalObrir(false)}
        proveidors={proveidors}
        onSelect={(p) => {
          setSelectedProveidor(p);
          setValue("proveidorId", p.id, {
            shouldValidate: true,
            shouldDirty: true,
          });
        }}
      />

      {/* ----- Modal de canvis ----- */}
      <HistoricCanvisModal
        opened={modalObert}
        onClose={close}
        canvis={canvis}
      />

      <Title order={3} mb="md" style={{ fontFamily: "Poppins, sans-serif" }}>
        Editar Albarà
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
          <TextInput
            size="sm"
            label="ID"
            value={albaraId.toString()}
            disabled
          />
          <TextInput
            size="sm"
            label="Estat"
            value={item.estat}
            disabled
            styles={{
              input: {
                backgroundColor:
                  item.estat === "VALIDAT"
                    ? "#d4edda" // Fondo verde si es VALIDAT
                    : item.estat === "EN_CURS"
                    ? "#fff3cd" // Fondo naranja claro si es EN_CURS
                    : undefined, // Fondo predeterminado para otros valores
                color:
                  item.estat === "VALIDAT"
                    ? "#155724" // Texto verde si es VALIDAT
                    : item.estat === "EN_CURS"
                    ? "#856404" // Texto naranja oscuro si es EN_CURS
                    : undefined, // Texto predeterminado para otros valores
              },
              label: {
                color:
                  item.estat === "VALIDAT"
                    ? "#155724" // Color del label en verde si es VALIDAT
                    : item.estat === "EN_CURS"
                    ? "#856404" // Color del label en naranja si es EN_CURS
                    : undefined, // Color predeterminado para otros valores
              },
            }}
          />
          <TextInput size="sm" label="Tipus" value={item.tipus} disabled />
          <TextInput
            size="sm"
            label="Factura ID"
            value={hasFactura ? item.factura!.id.toString() : "No assignada"}
            disabled
          />
          {hasFactura && (
            <Text
              size="sm"
              mt="xs"
              component="a"
              href={`/factures/${item.factura!.id}`}
              target="_blank"
              rel="noopener noreferrer"
              style={{ color: "#1a73e8", textDecoration: "underline" }}
            >
              Factura #{item.factura!.id}
            </Text>
          )}
        </Group>

        <Group grow mb="md" style={{ display: "flex", gap: "16px" }}>
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

          {/* Número controlat via Controller per evitar conflictes de tipus */}
          <Controller
            name="importTotal"
            control={control}
            rules={{ required: "L'import total és obligatori" }}
            render={({ field }: { field: any }) => (
              <NumberInput
                size="sm"
                label="Import Total"
                step={0.01}
                min={0}
                max={1_000_000}
                {...field}
              />
            )}
          />

          {/* ------ Selector de proveïdor via modal ------ */}
          <input
            type="hidden"
            {...register("proveidorId", { required: true })}
          />
          <Flex align="flex-end" gap="sm">
            <TextInput
              size="sm"
              label="Proveïdor"
              placeholder="Clica per triar"
              readOnly
              value={selectedProveidor?.nomComercial || ""}
              error={errors.proveidorId && "Obligatori"}
              onClick={() => setModalObrir(true)}
            />
            <Button size="sm" onClick={() => setModalObrir(true)}>
              Tria
            </Button>
          </Flex>
        </Group>

        <Group
          grow
          mb="md"
          style={{ display: "flex", gap: "16px", marginTop: "20px" }}
        >
          <TextInput
            size="sm"
            label="Fitxer Adjunt"
            placeholder="URL fitxer adjunt"
            value={item.fitxerAdjunt ?? ""}
            disabled
          />
          {item.fitxerAdjunt ? (
            <div style={{ display: "flex", flexDirection: "column", gap: 8 }}>
              {(() => {
                const fileUrl = `${API_BASE_URL}/fitxers/albara/${albaraId}`;
                const isImg = /\.(png|jpe?g)$/i.test(item.fitxerAdjunt || "");
                return (
                  <>
                    <a
                      href={`${API_BASE_URL}/fitxers/albara/${albaraId}`}
                      target="_blank"
                      rel="noopener noreferrer"
                      style={{
                        color: "#1a73e8",
                        textDecoration: "underline",
                        fontSize: "0.9rem"
                      }}
                    >
                      Veure fitxer adjunt
                    </a>
                    {isImg && (
                      <img
                        src={fileUrl}
                        alt="Fitxer adjunt"
                        style={{
                          maxWidth: 200,
                          border: "1px solid #ccc",
                          padding: 4,
                        }}
                      />
                    )}
                  </>
                );
              })()}
            </div>
          ) : (
            <Text size="sm" color="dimmed">
              No hi ha fitxer adjunt
            </Text>
          )}
        </Group>
        <Group
          grow
          mb="md"
          style={{ display: "flex", gap: "16px", marginTop: "20px" }}
        >
          <div style={{ flexGrow: 1 }}>
            <Group justify="apart" mt="xl" style={{ marginTop: "40px" }}>
              {!hasFactura && (
                <>
                  <Button
                    size="sm"
                    type="submit"
                    loading={updateMutation.status === "pending"}
                  >
                    Desa
                  </Button>
                  <Button
                    color="red"
                    variant="outline"
                    onClick={handleDelete}
                    style={{ marginLeft: "16px" }}
                  >
                    Elimina
                  </Button>
                </>
              )}
              <Button
                size="sm"
                variant="outline"
                onClick={() => navigate("/albarans")}
                style={{ marginLeft: "16px" }}
              >
                Cancel·la
              </Button>
            </Group>
          </div>
        </Group>

        <div style={{ flexGrow: 1, marginTop: "40px" }}>
          <text style={{ fontWeight: "bold" }}>Històric de canvis:</text>
          <Group
            grow
            mb="md"
            style={{ display: "flex", gap: "16px", marginTop: "10px" }}
          >
            <TextInput
              size="sm"
              label="Creat per"
              value={nomCreador}
              disabled
            />
            <TextInput
              size="sm"
              label="Data de creació"
              value={dataCreacio}
              disabled
            />
            <TextInput
              size="sm"
              label="Última modificació"
              value={dataModificacio}
              disabled
            />
            <TextInput
              size="sm"
              label="Modificat per"
              value={nomModificador}
              disabled
            />
            <Group justify="flex-end" mt="md">
              <Button onClick={open}>Veure Històric canvis</Button>
            </Group>{" "}
          </Group>
        </div>
      </form>
    </Paper>
  );
}
