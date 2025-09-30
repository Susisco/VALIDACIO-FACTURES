import React, { useState } from "react";
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
  Popover,
  ScrollArea,
  Box,
  Text,
} from "@mantine/core";
import { showNotification } from "@mantine/notifications";
import { useProveidors } from "../api/proveidors";
import { useCreatePressupost } from "../api/pressupostos";
import type { PressupostInput } from "../api/pressupostos";
import { useEffect } from "react";
import { CLIENT_PLATFORM_HEADER, WEB_PLATFORM_VALUE } from "../api/client";
import { API_BASE_URL } from "../config/constants";

export default function PressupostFormNew() {
  const navigate = useNavigate();
  const { data: proveidors = [] } = useProveidors();
  const createMutation = useCreatePressupost();

  const {
    register,
    control,
    handleSubmit,
    formState: { errors },
  } = useForm<PressupostInput>({
    defaultValues: {
      tipus: "PRESSUPOST",
      referenciaDocument: "",
      data: "",
      importTotal: 0,
      estat: "PENDENT",
      creadorId: 1,
      validatPerId: null,
      proveidorId: 0,
      edificiId: 0,
      otsId: 0,
      facturaId: 0,
      fitxerAdjunt: null,
    },
  });

  const [opened, setOpened] = useState(false);
  const [selectedLabel, setSelectedLabel] = useState("");
  const [file, setFile] = useState<File | null>(null);
  const [fileError, setFileError] = useState<string | null>(null);
  const [previewUrl, setPreviewUrl] = useState<string | null>(null);

  //Neteja la memòria amb useEffect
  useEffect(() => {
    return () => {
      if (previewUrl) {
        URL.revokeObjectURL(previewUrl);
      }
    };
  }, [previewUrl]);

  const onSubmit: SubmitHandler<PressupostInput> = (data) => {
    // Validació del fitxer abans de crear
    if (!file) {
      setFileError("El fitxer és obligatori");
      return;
    }
    setFileError(null);

    const payload: PressupostInput = {
      ...data,
      creadorId: data.creadorId ?? null,
      validatPerId: data.validatPerId ?? null,
      proveidorId: data.proveidorId,
      edificiId: data.edificiId && data.edificiId > 0 ? data.edificiId : null,
      otsId: data.otsId && data.otsId > 0 ? data.otsId : null,
      facturaId: data.facturaId && data.facturaId > 0 ? data.facturaId : null,
      fitxerAdjunt: null,
    };

    createMutation.mutate(payload, {
      onSuccess: async (created) => {
        const formData = new FormData();
        formData.append("file", file);

        try {
          await fetch(
            `${API_BASE_URL}/fitxers/pressupost/${created.id}`,
            {
              method: "POST",
              body: formData,
              headers: {
                [CLIENT_PLATFORM_HEADER]: WEB_PLATFORM_VALUE,
              },
            }
          );
        } catch (error) {
          showNotification({
            title: "Error al pujar el fitxer",
            message: String(error),
            color: "red",
          });
        }

        showNotification({
          title: "Correcte",
          message: "Pressupost creat",
          color: "teal",
        });
        navigate("/pressupostos");
      },
      onError: (err) => {
        const msg = err instanceof Error ? err.message : "No s'ha pogut crear";
        showNotification({ title: "Error", message: msg, color: "red" });
      },
    });
  };

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
      <Title order={3} mb="md">
        Nou Pressupost
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

          {/* Proveïdor */}
          <Controller
            name="proveidorId"
            control={control}
            rules={{ required: true }}
            render={({ field }) => (
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
                      value={selectedLabel}
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
                          setSelectedLabel(p.nomComercial);
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
            )}
          />

          <TextInput
            size="sm"
            label="Fitxer Adjunt"
            placeholder="URL fitxer adjunt"
            {...register("fitxerAdjunt")}
            disabled
          />

          {/* Input per al fitxer */}
          <div>
            <input
              type="file"
              accept=".jpg,.jpeg,.png,.pdf"
              onChange={(e) => {
                if (e.target.files && e.target.files.length > 0) {
                  const selected = e.target.files[0];
                  setFile(selected);
                  setFileError(null);

                  // Si és imatge, mostra preview
                  if (selected.type.startsWith("image/")) {
                    const objectUrl = URL.createObjectURL(selected);
                    setPreviewUrl(objectUrl);
                  } else {
                    setPreviewUrl(null); // no mostrem preview si no és imatge
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

        <div style={{ marginTop: "24px" }}>
          <Group justify="space-between" mt="xl">
            <Button type="submit" loading={createMutation.isLoading}>
              Crea
            </Button>
            <Button variant="outline" onClick={() => navigate("/pressupostos")}>
              Cancel·la
            </Button>
          </Group>
        </div>
      </form>
    </Paper>
  );
}
