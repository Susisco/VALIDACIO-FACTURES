import React, { useEffect, useState } from "react";
import { useForm, SubmitHandler, Controller } from "react-hook-form";
import { useNavigate } from "react-router-dom";
import {
  Paper,
  Flex,
  TextInput,
  NumberInput,
  Button,
  Title,
  Group,
  Box,
  Text,
} from "@mantine/core";
import { showNotification } from "@mantine/notifications";
import { useProveidors } from "../api/proveidors";
import { useSaveAlbaraWithFile } from "../api/albarans";
import type { AlbaraInput } from "../api/albarans";
import ModalProveidorSelector from "../components/ModalProveidorSelector";
import type { Proveidor } from "../api/proveidors";

export default function AlbaraFormNew() {
  const navigate = useNavigate();
  const { data: proveidors = [] } = useProveidors();
  const saveAlbaraWithFileMutation = useSaveAlbaraWithFile();

  const [userId, setUserId] = useState<number | null>(null);
  const [modalObrir, setModalObrir] = useState(false);
  const [selectedProveidor, setSelectedProveidor] = useState<Proveidor | null>(
    null
  );
  const [file, setFile] = useState<File | null>(null);
  const [fileError, setFileError] = useState<string | null>(null);
  const [previewUrl, setPreviewUrl] = useState<string | null>(null);

  // Obtenim l'usuari actual al carregar el component
  useEffect(() => {
    const id = localStorage.getItem("usuariId");
    setUserId(id ? parseInt(id, 10) : 0);
  }, []);

  // Alliberem l'objecte URL creat per la vista prèvia del fitxer
  useEffect(() => {
    return () => {
      if (previewUrl) URL.revokeObjectURL(previewUrl);
    };
  }, [previewUrl]);

  // Configuració del formulari amb react-hook-form
  const {
    register,
    control,
    handleSubmit,
    setValue,
    formState: { errors },
  } = useForm<AlbaraInput>({
    defaultValues: {
      tipus: "ALBARA",
      referenciaDocument: "",
      data: "",
      importTotal: 0,
      estat: "PENDENT",
      creadorId: userId ?? 0,
      validatPerId: null,
      proveidorId: 0,
      edificiId: 0,
      otsId: 0,
      facturaId: 0,
      fitxerAdjunt: null,
    },
  });

  // Funció que s'executa quan l'usuari envia el formulari
  const onSubmit: SubmitHandler<AlbaraInput> = (data) => {
    if (!file) {
      setFileError("El fitxer és obligatori");
      return;
    }
    setFileError(null);

    saveAlbaraWithFileMutation.mutate(
      { data, file },
      {
        onSuccess: () => {
          showNotification({
            title: "Correcte",
            message: "Albarà creat i fitxer pujat correctament",
            color: "teal",
          });
          navigate("/albarans");
        },
        onError: (error) => {
          showNotification({
            title: "Error",
            message: String(error),
            color: "red",
          });
        },
      }
    );
  };

  // Mostrar missatges de càrrega o error si no es pot obtenir l'usuari
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
      style={{
        maxWidth: 600,
        margin: "auto",
        fontFamily: "Poppins, sans-serif",
      }}
    >
      {/* Modal de selecció de proveïdor */}
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
        Nou Albarà
      </Title>

      <form onSubmit={handleSubmit(onSubmit)}>
        <Flex direction="column" gap="md">
          <TextInput size="sm" label="Estat" {...register("estat")} disabled />
          <TextInput size="sm" label="Tipus" {...register("tipus")} disabled />
          <TextInput
            size="sm"
            label="Referència"
            {...register("referenciaDocument", { required: true })}
            error={errors.referenciaDocument && "Obligatori"}
          />
          <TextInput
            size="sm"
            label="Data"
            type="date"
            {...register("data", { required: true })}
            error={errors.data && "Obligatori"}
          />
          <Controller
            name="importTotal"
            control={control}
            render={({ field }) => (
              <NumberInput
                size="sm"
                label="Import Total"
                step={0.01}
                {...field}
              />
            )}
          />

          {/* Selector de proveïdor reutilitzant el modal */}
          <Flex align="flex-end" gap="sm">
            <TextInput
              size="sm"
              label="Proveïdor"
              placeholder="Clica per triar"
              value={selectedProveidor?.nomComercial || ""}
              readOnly
              error={errors.proveidorId && "Obligatori"}
              onClick={() => setModalObrir(true)}
            />
            <Button size="sm" onClick={() => setModalObrir(true)}>
              Tria
            </Button>
          </Flex>

          {/* Gestor de fitxers */}
          <div>
            <input
              type="file"
              accept=".jpg,.jpeg,.png,.pdf"
              onChange={(e) => {
                if (e.target.files && e.target.files.length > 0) {
                  const selected = e.target.files[0];
                  setFile(selected);
                  setFileError(null);
                  if (selected.type.startsWith("image/")) {
                    const objectUrl = URL.createObjectURL(selected);
                    setPreviewUrl(objectUrl);
                  } else {
                    setPreviewUrl(null);
                  }
                }
              }}
            />
            {fileError && (
              <Text size="sm" color="red" mt={4}>
                {fileError}
              </Text>
            )}
            {previewUrl && (
              <Box mt="sm">
                <Text size="xs" color="dimmed" mb={4}>
                  Vista prèvia:
                </Text>
                <img
                  src={previewUrl}
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
          </div>
        </Flex>

        <Group justify="space-between" mt="xl">
          <Button
            type="submit"
            disabled={saveAlbaraWithFileMutation.status === "pending"}
          >
            Crea
          </Button>
          <Button variant="outline" onClick={() => navigate("/albarans")}>
            Cancel·la
          </Button>
        </Group>
      </form>
    </Paper>
  );
}
